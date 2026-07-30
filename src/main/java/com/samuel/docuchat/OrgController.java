package com.samuel.docuchat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orgs")
public class OrgController {

    private final OrgRepository orgRepository;

    public OrgController(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    @PostMapping
    public ResponseEntity<Org> createOrg(@RequestBody Org org) {
        Org saved = orgRepository.save(org);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Org> listOrgs() {
        return orgRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Org> getOrg(@PathVariable UUID id) {
        return orgRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}