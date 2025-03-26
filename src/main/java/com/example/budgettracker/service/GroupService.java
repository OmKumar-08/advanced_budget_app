package com.example.budgettracker.service;

import com.example.budgettracker.domain.Group;
import com.example.budgettracker.domain.User;
import com.example.budgettracker.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    @Transactional
    public Group createGroup(Group group) {
        if (group.getCreator() == null) {
            throw new IllegalArgumentException("Group creator cannot be null");
        }
        group.getMembers().add(group.getCreator());
        return groupRepository.save(group);
    }

    @Transactional
    public Group updateGroup(Long groupId, Group updatedGroup) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        group.setType(updatedGroup.getType());

        return groupRepository.save(group);
    }

    @Transactional
    public void addMemberToGroup(Long groupId, User user) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        group.getMembers().add(user);
        groupRepository.save(group);
    }

    @Transactional
    public void removeMemberFromGroup(Long groupId, User user) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (user.equals(group.getCreator())) {
            throw new IllegalArgumentException("Cannot remove the group creator");
        }

        if (groupRepository.hasUnsettledTransactions(groupId)) {
            throw new IllegalStateException("Cannot remove member with unsettled transactions");
        }

        group.getMembers().remove(user);
        groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<Group> getUserGroups(User user) {
        return groupRepository.findByMembers(user);
    }

    @Transactional(readOnly = true)
    public List<Group> getGroupsByType(Group.GroupType type, User user) {
        return groupRepository.findByTypeAndMember(type, user);
    }

    @Transactional(readOnly = true)
    public Optional<Group> getGroupWithMembers(Long groupId) {
        return groupRepository.findByIdWithMembers(groupId);
    }

    @Transactional(readOnly = true)
    public Optional<Group> getGroupWithTransactions(Long groupId) {
        return groupRepository.findByIdWithTransactions(groupId);
    }

    @Transactional(readOnly = true)
    public boolean hasUnsettledTransactions(Long groupId) {
        return groupRepository.hasUnsettledTransactions(groupId);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (hasUnsettledTransactions(groupId)) {
            throw new IllegalStateException("Cannot delete group with unsettled transactions");
        }

        groupRepository.delete(group);
    }
}