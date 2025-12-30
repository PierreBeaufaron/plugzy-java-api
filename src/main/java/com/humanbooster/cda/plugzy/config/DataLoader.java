package com.humanbooster.cda.plugzy.config;

import com.humanbooster.cda.plugzy.entity.Role;
import com.humanbooster.cda.plugzy.entity.User;
import com.humanbooster.cda.plugzy.repository.RoleRepository;
import com.humanbooster.cda.plugzy.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // ---------- ROLES ----------
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("ROLE_USER");
                        return roleRepository.save(r);
                    });

            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("ROLE_ADMIN");
                        return roleRepository.save(r);
                    });

            // ---------- USERS ----------
            if (userRepository.findByEmail("user@plugzy.test").isEmpty()) {
                User user = new User();
                user.setEmail("user@plugzy.test");
                user.setPassword(passwordEncoder.encode("password"));
                user.setRole(roleUser);

                // champs obligatoires
                user.setPhone("0600000000");
                user.setUsername("user");
                user.setVerified(true);

                userRepository.save(user);
            }

            if (userRepository.findByEmail("admin@plugzy.test").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@plugzy.test");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(roleAdmin);

                admin.setPhone("0600000001");
                admin.setUsername("admin");
                admin.setVerified(true);

                userRepository.save(admin);
            }
        };
    }
}
