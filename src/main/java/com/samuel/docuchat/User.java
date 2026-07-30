package com.samuel.docuchat;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public User() {}

    public User(String email, String passwordHash, Org org) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.org = org;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Org getOrg() { return org; }
    public void setOrg(Org org) { this.org = org; }
    public Instant getCreatedAt() { return createdAt; }
}