package com.example.budgettracker.service;

import com.example.budgettracker.domain.Group;
import com.example.budgettracker.domain.Transaction;
import com.example.budgettracker.domain.Settlement;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.repository.TransactionRepository;
import com.example.budgettracker.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupExpenseService {
    private final TransactionRepository transactionRepository;
    private final SettlementRepository settlementRepository;
    private final GroupService groupService;

    @Transactional
    public Transaction createGroupExpense(Transaction expense, Map<User, BigDecimal> customShares) {
        if (expense.getGroup() == null) {
            throw new IllegalArgumentException("Group must be specified for group expense");
        }

        expense.setType(Transaction.TransactionType.BILL_SPLIT);
        expense = transactionRepository.save(expense);

        Group group = groupService.getGroupWithMembers(expense.getGroup().getId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        createSettlements(expense, group.getMembers(), customShares);
        return expense;
    }

    private void createSettlements(Transaction expense, Set<User> members, Map<User, BigDecimal> customShares) {
        User payer = expense.getUser();
        BigDecimal totalAmount = expense.getAmount();

        Map<User, BigDecimal> shares = calculateShares(members, totalAmount, customShares);

        for (Map.Entry<User, BigDecimal> entry : shares.entrySet()) {
            User member = entry.getKey();
            BigDecimal share = entry.getValue();

            if (!member.equals(payer) && share.compareTo(BigDecimal.ZERO) > 0) {
                Settlement settlement = new Settlement();
                settlement.setTransaction(expense);
                settlement.setPayer(member);
                settlement.setPayee(payer);
                settlement.setAmount(share);
                settlement.setStatus(Settlement.SettlementStatus.PENDING);
                settlement.setDueDate(expense.getTransactionDate().plusDays(7));
                settlementRepository.save(settlement);
            }
        }
    }

    private Map<User, BigDecimal> calculateShares(Set<User> members, BigDecimal totalAmount,
                                                Map<User, BigDecimal> customShares) {
        Map<User, BigDecimal> shares = new HashMap<>();

        if (customShares != null && !customShares.isEmpty()) {
            BigDecimal totalShares = customShares.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalShares.compareTo(BigDecimal.ONE) != 0) {
                throw new IllegalArgumentException("Custom shares must sum to 1");
            }
            for (User member : members) {
                shares.put(member, customShares.getOrDefault(member, BigDecimal.ZERO)
                        .multiply(totalAmount).setScale(2, RoundingMode.HALF_UP));
            }
        } else {
            BigDecimal equalShare = totalAmount.divide(BigDecimal.valueOf(members.size()), 2, RoundingMode.HALF_UP);
            for (User member : members) {
                shares.put(member, equalShare);
            }
        }

        return shares;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getGroupExpenses(Group group) {
        return transactionRepository.findByGroupAndType(group, Transaction.TransactionType.BILL_SPLIT);
    }

    @Transactional(readOnly = true)
    public Map<User, BigDecimal> getGroupBalances(Group group) {
        List<Settlement> settlements = settlementRepository.findByTransactionIdIn(
                getGroupExpenses(group).stream().map(Transaction::getId).toList());


        Map<User, BigDecimal> balances = new HashMap<>();
        for (Settlement settlement : settlements) {
            if (settlement.getStatus() == Settlement.SettlementStatus.PENDING) {
                balances.merge(settlement.getPayer(), settlement.getAmount(), BigDecimal::add);
                balances.merge(settlement.getPayee(), settlement.getAmount().negate(), BigDecimal::add);
            }
        }
        return balances;
    }
}