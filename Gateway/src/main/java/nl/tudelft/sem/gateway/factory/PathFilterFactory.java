package nl.tudelft.sem.gateway.factory;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.gateway.valueobject.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Creates a path filter. When the apply method is invoked
 * the path  is rewritten to include the ID of the student or the company
 * who is sending the request.
 * The apply method is invoked when an authenticated user sends a request to either
 * the student or company microservices.
 *
 * @Sources: https://www.baeldung.com/spring-cloud-gateway-routing-predicate-factories
 */
@Component
public class PathFilterFactory extends AbstractGatewayFilterFactory<PathFilterFactory.Config> {

    @Autowired
    private AuthenticationResponse authenticationResponse;

    public PathFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().toString();
            // This is terrible but it works
            int first = Math.max(path.indexOf("/student/"), path.indexOf("/company/"));
            assert first >= 0;
            path = path.substring(first);
            path = path.replaceFirst("/student/",
                    "/student/" + authenticationResponse.getUsername() + "/");
            path = path.replaceFirst("/company/",
                    "/company/" + authenticationResponse.getUsername() + "/");
            // -----------------------------------------
            final String newPath = path;
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(originalRequest -> originalRequest.uri(UriComponentsBuilder
                            .fromUri(exchange.getRequest().getURI())
                            .replacePath(newPath)
                            .build()
                            .toUri()))
                    .build();
            return chain.filter(modifiedExchange);
        };
    }

    @Validated
    @Data
    @NoArgsConstructor
    public static  class Config {

    }
}
