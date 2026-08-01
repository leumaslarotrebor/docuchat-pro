package com.samuel.docuchat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final OrgRepository orgRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final JwtService jwtService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;

    public DocumentController(
            DocumentRepository documentRepository,
            OrgRepository orgRepository,
            DocumentChunkRepository documentChunkRepository,
            JwtService jwtService,
            ChunkingService chunkingService,
            EmbeddingService embeddingService
    ) {
        this.documentRepository = documentRepository;
        this.orgRepository = orgRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.jwtService = jwtService;
        this.chunkingService = chunkingService;
        this.embeddingService = embeddingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file
    ) {
        UUID orgId;
        try {
            orgId = jwtService.extractOrgId(authHeader);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or missing token"));
        }

        var orgOpt = orgRepository.findById(orgId);
        if (orgOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Org not found"));
        }

        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Could not read file"));
        }

        Document doc = new Document(file.getOriginalFilename(), orgOpt.get());
        doc.setContent(content);
        doc.setStatus(Document.Status.PROCESSING);
        Document saved = documentRepository.save(doc);

        try {
            List<String> chunks = chunkingService.chunk(content);
            for (String chunkText : chunks) {
                String embedding = embeddingService.embedAsString(chunkText);
                DocumentChunk chunk = new DocumentChunk(chunkText, saved);
                chunk.setEmbedding(embedding);
                documentChunkRepository.save(chunk);
            }
            saved.setStatus(Document.Status.READY);
        } catch (Exception e) {
            saved.setStatus(Document.Status.FAILED);
        }
        documentRepository.save(saved);

        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping
    public ResponseEntity<?> listDocuments(@RequestHeader("Authorization") String authHeader) {
        UUID orgId;
        try {
            orgId = jwtService.extractOrgId(authHeader);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or missing token"));
        }

        List<Map<String, Object>> docs = documentRepository.findByOrgId(orgId)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(docs);
    }

    private Map<String, Object> toDto(Document doc) {
        return Map.of(
            "id", doc.getId(),
            "filename", doc.getFilename(),
            "status", doc.getStatus(),
            "createdAt", doc.getCreatedAt()
        );
    }
}
