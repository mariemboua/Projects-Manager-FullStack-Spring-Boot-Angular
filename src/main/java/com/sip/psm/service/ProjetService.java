package com.sip.psm.service;

import com.sip.psm.entity.Client;
import com.sip.psm.entity.Projet;
import com.sip.psm.repository.ProjetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final ClientService clientService;

    public ProjetService(ProjetRepository projetRepository, ClientService clientService) {
        this.projetRepository = projetRepository;
        this.clientService = clientService;
    }

    public List<Projet> findAll() {
        return projetRepository.findAll();
    }

    public List<Projet> findByClient(Long clientId) {
        return projetRepository.findByClientId(clientId);
    }

    public Projet findById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet introuvable : " + id));
    }

    public Projet create(Projet projet) {
        projet.setId(null);
        projet.setClient(resolveClient(projet));
        return projetRepository.save(projet);
    }

    public Projet update(Long id, Projet data) {
        Projet existing = findById(id);
        existing.setLibelle(data.getLibelle());
        existing.setBudget(data.getBudget());
        existing.setDuree(data.getDuree());
        existing.setDescription(data.getDescription());
        existing.setClient(resolveClient(data));
        return projetRepository.save(existing);
    }

    public void delete(Long id) {
        projetRepository.delete(findById(id));
    }

    // Vérifie que le client envoyé dans le JSON existe bien
    private Client resolveClient(Projet projet) {
        if (projet.getClient() == null || projet.getClient().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le client (id) est obligatoire");
        }
        return clientService.findById(projet.getClient().getId());
    }
}