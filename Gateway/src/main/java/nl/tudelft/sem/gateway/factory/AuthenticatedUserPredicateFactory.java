package nl.tudelft.sem.gateway.factory;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.gateway.service.AuthenticatedUserService;
import nl.tudelft.sem.gateway.valueobject.AuthenticationResponse;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

/**
 * Creates a predicate that checks whether a request sent to
 * the student or company microservice contains an Authorization
 * header with a jwt token. The jwt token is validated by sending a
 * request to the authentication microservice.
 *
 * @Sources: https://www.baeldung.com/spring-cloud-gateway
 */
public class AuthenticatedUserPredicateFactory extends
        AbstractRoutePredicateFactory<AuthenticatedUserPredicateFactory.Config> {

    private final AuthenticatedUserService authenticatedUserService;

    public AuthenticatedUserPredicateFactory(AuthenticatedUserService authenticatedUserService) {
        super(Config.class);
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return (ServerWebExchange serverWebExchange) -> {
            List<String> authHeader = serverWebExchange
                    .getRequest()
                    .getHeaders()
                    .get("Authorization");
            boolean isAuthenticated;
            if (authHeader == null || authHeader.size() == 0) {
                isAuthenticated = false;
            } else {
                AuthenticationResponse response = authenticatedUserService
                        .isAuthenticated(authHeader.get(0));
                isAuthenticated = response.isTokenIsValid();
                if (isAuthenticated) {
                    String role = response
                            .getRole()
                            .replace("ROLE_", "")
                            .toLowerCase(Locale.ENGLISH);
                    // Set isAuthenticated to false if a student is trying to use company features
                    isAuthenticated = serverWebExchange
                            .getRequest()
                            .getURI()
                            .toString()
                            .contains(role);
                }
            }
            return isAuthenticated;
        };
    }

    @Validated
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {

        private boolean isAuthenticated = true;
    }
}
