package com.example.budgettracker.repository;

import com.example.budgettracker.domain.Group;
import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreator(User creator);
    
    List<Group> findByMembers(User member);
    
    @Query("SELECT g FROM Group g WHERE g.type = :type AND :user MEMBER OF g.members")
    List<Group> findByTypeAndMember(Group.GroupType type, User user);
    
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.members WHERE g.id = :id")
    Optional<Group> findByIdWithMembers(Long id);
    
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.transactions WHERE g.id = :id")
    Optional<Group> findByIdWithTransactions(Long id);
    
    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.group.id = :groupId AND t.settled = false")
    boolean hasUnsettledTransactions(Long groupId);
}