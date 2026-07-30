package com.samuel.docuchat;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String filename;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum Status { PENDING, PROCESSING, READY, FAILED }

    public Document() {}

    public Document(String filename, Org org) {
        this.filename = filename;
        this.org = org;
    }

    public UUID getId() { return id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Org getOrg() { return org; }
    public void setOrg(Org org) { this.org = org; }
    public Instant getCreatedAt() { return createdAt; }
}