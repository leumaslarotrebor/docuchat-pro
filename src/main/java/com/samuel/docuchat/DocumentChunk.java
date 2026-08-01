package com.samuel.docuchat;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "embedding", columnDefinition = "TEXT")
    private String embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public DocumentChunk() {}

    public DocumentChunk(String content, Document document) {
        this.content = content;
        this.document = document;
    }

    public UUID getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getEmbedding() { return embedding; }
    public void setEmbedding(String embedding) { this.embedding = embedding; }
    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }
    public Instant getCreatedAt() { return createdAt; }
}
