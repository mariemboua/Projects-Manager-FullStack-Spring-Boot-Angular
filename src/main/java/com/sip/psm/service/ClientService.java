package com.sip.psm.service;

import com.sip.psm.entity.Client;
import com.sip.psm.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable : " + id));
    }

    public Client create(Client client) {
        client.setId(null);
        return clientRepository.save(client);
    }

    public Client update(Long id, Client data) {
        Client existing = findById(id);
        existing.setNom(data.getNom());
        existing.setTel(data.getTel());
        existing.setEmail(data.getEmail());
        existing.setAdresse(data.getAdresse());
        return clientRepository.save(existing);
    }

    public void delete(Long id) {
        Client existing = findById(id);
        clientRepository.delete(existing);
    }
}