package com.sip.psm.controller;

import com.sip.psm.entity.Projet;
import com.sip.psm.service.ProjetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
public class ProjetController {

    private final ProjetService projetService;

    public ProjetController(ProjetService projetService) {
        this.projetService = projetService;
    }

    @GetMapping
    public List<Projet> getAll() {
        return projetService.findAll();
    }

    @GetMapping("/client/{clientId}")
    public List<Projet> getByClient(@PathVariable Long clientId) {
        return projetService.findByClient(clientId);
    }

    @GetMapping("/{id}")
    public Projet getById(@PathVariable Long id) {
        return projetService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Projet create(@RequestBody Projet projet) {
        return projetService.create(projet);
    }

    @PutMapping("/{id}")
    public Projet update(@PathVariable Long id, @RequestBody Projet projet) {
        return projetService.update(id, projet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        projetService.delete(id);
    }
}