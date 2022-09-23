package com.thiagosena.service.email.application.configs;

import com.thiagosena.email.Email;
import com.thiagosena.service.email.resource.repositories.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabasePreset {
    private static final Logger logger = LoggerFactory.getLogger(DatabasePreset.class);

    @Bean
    CommandLineRunner initDatabase(EmailRepository repository) {
        return args -> {
            logger.info("Preloading " + repository.save(new Email("goku@email.com.", "{preloaded-report}")));
            logger.info("Preloading " + repository.save(new Email("frodo@email.com", "{preloaded-report}")));
        };
    }
}