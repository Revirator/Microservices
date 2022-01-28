package nl.tudelft.sem.gateway.config;

import nl.tudelft.sem.gateway.factory.AuthenticatedUserPredicateFactory;
import nl.tudelft.sem.gateway.service.AuthenticatedUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthPredicateConfig {

    @Bean
    public AuthenticatedUserPredicateFactory authenticatedUser(
            AuthenticatedUserService authenticatedUserService) {
        return new AuthenticatedUserPredicateFactory(authenticatedUserService);
    }
}
