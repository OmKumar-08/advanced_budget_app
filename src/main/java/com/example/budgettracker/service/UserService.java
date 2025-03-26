package com.example.budgettracker.service;

import com.example.budgettracker.domain.User;
import com.example.budgettracker.domain.UserProfile;
import com.example.budgettracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public User registerUser(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(Set.of("USER"));
        user.setEnabled(true);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        user.setProfile(profile);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithProfile(Long userId) {
        return userRepository.findByIdWithProfile(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithTransactions(Long userId) {
        return userRepository.findByIdWithTransactions(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithGroups(Long userId) {
        return userRepository.findByIdWithGroups(userId);
    }

    @Transactional
    public User updateUserProfile(Long userId, UserProfile updatedProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = user.getProfile();
        profile.setCurrency(updatedProfile.getCurrency());
        profile.setMonthlyBudget(updatedProfile.getMonthlyBudget());
        profile.setSavingsGoal(updatedProfile.getSavingsGoal());
        profile.setEmailNotificationsEnabled(updatedProfile.isEmailNotificationsEnabled());
        profile.setPushNotificationsEnabled(updatedProfile.isPushNotificationsEnabled());
        profile.setSmsNotificationsEnabled(updatedProfile.isSmsNotificationsEnabled());
        profile.setBudgetAlertThreshold(updatedProfile.getBudgetAlertThreshold());

        return userRepository.save(user);
    }
}