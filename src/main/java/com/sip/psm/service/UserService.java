package com.sip.psm.service;

import com.sip.psm.entity.Role;
import com.sip.psm.entity.User;
import com.sip.psm.repository.RoleRepository;
import com.sip.psm.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable : " + id));
    }

    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(resolveRoles(user.getRoles()));
        return userRepository.save(user);
    }

    public User update(Long id, User data) {
        User existing = findById(id);
        existing.setEmail(data.getEmail());
        existing.setTel(data.getTel());
        existing.setNom(data.getNom());
        existing.setPrenom(data.getPrenom());
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        if (data.getRoles() != null && !data.getRoles().isEmpty()) {
            existing.setRoles(resolveRoles(data.getRoles()));
        }
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        userRepository.delete(findById(id));
    }
    
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        Role collaborateur = roleRepository.findByLibelle("COLLABORATEUR")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Rôle COLLABORATEUR absent"));
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(Set.of(collaborateur)));
        return userRepository.save(user);
    }

    // Remplace les rôles reçus (identifiés par id ou libellé) par les entités réelles de la base
    private Set<Role> resolveRoles(Set<Role> input) {
        Set<Role> result = new HashSet<>();
        if (input == null) {
            return result;
        }
        for (Role r : input) {
            Role role;
            if (r.getId() != null) {
                role = roleRepository.findById(r.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle inconnu : " + r.getId()));
            } else {
                role = roleRepository.findByLibelle(r.getLibelle())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle inconnu : " + r.getLibelle()));
            }
            result.add(role);
        }
        return result;
    }
}