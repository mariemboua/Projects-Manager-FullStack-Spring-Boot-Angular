package com.sip.psm.config;

import com.sip.psm.entity.Role;
import com.sip.psm.entity.User;
import com.sip.psm.repository.RoleRepository;
import com.sip.psm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Role admin = roleRepository.findByLibelle("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
            roleRepository.findByLibelle("COLLABORATEUR")
                    .orElseGet(() -> roleRepository.save(new Role("COLLABORATEUR")));

            if (!userRepository.existsByEmail("admin@psm.tn")) {
                User u = new User("admin@psm.tn", passwordEncoder.encode("admin123"),
                        "20123456", "Admin", "PSM");
                u.getRoles().add(admin);
                userRepository.save(u);
            }
        };
    }
}
