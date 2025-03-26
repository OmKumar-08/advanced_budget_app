package com.example.budgettracker.domain;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;

@Entity
public class User implements UserDetails {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    // Add getter for email
    public String getEmail() {
        return email;
    }

    // Add setter for email if needed
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false)
    private String password;

    // Add getter already exists for UserDetails interface
    
    // Add setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(nullable = false)
    private String firstName;

    // Add getter and setter for firstName
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(nullable = false)
    private String lastName;

    // Add getter and setter for lastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    // Add getter for roles
    public Set<String> getRoles() {
        return roles;
    }

    // Add setter for roles if needed
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    // Add getter and setter for profile
    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    @OneToMany(mappedBy = "user")
    private Set<Transaction> transactions = new HashSet<>();

    @ManyToMany(mappedBy = "members")
    private Set<Group> groups = new HashSet<>();

    private boolean enabled = true;

    // Add setter for enabled
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // @Override
    // public boolean isEnabled() {
    //     return true;
    // }
}