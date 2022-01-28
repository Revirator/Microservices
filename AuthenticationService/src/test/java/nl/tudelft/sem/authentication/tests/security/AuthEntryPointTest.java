package nl.tudelft.sem.authentication.tests.security;

import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.authentication.security.AuthEntryPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointTest {

    private final AuthEntryPoint authEntryPoint = new AuthEntryPoint();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationException exception;

    @Test
    public void testCommence() throws Exception {
        authEntryPoint.commence(request, response, exception);
        verify(response).sendError(401);
    }
}
