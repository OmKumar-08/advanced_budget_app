package com.example.budgettracker.repository;

import com.example.budgettracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(Long id);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.transactions WHERE u.id = :id")
    Optional<User> findByIdWithTransactions(Long id);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groups WHERE u.id = :id")
    Optional<User> findByIdWithGroups(Long id);
}