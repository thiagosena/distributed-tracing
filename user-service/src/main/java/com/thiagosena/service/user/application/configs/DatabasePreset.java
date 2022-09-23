package com.thiagosena.service.user.application.configs;

import com.thiagosena.service.user.resource.repositories.UserRepository;
import com.thiagosena.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DatabasePreset {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePreset.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {

        return args -> {
            logger.info("Preloading " + repository.save(new User("joquebede")));
            logger.info("Preloading " + repository.save(new User("antonieta")));
        };
    }
}
