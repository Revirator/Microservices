package nl.tudelft.sem.authentication.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.transaction.Transactional;
import nl.tudelft.sem.authentication.entity.User;
import nl.tudelft.sem.authentication.repository.UserRepository;
import nl.tudelft.sem.authentication.valueobjects.RequestUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    private final Gson gson = new GsonBuilder().create();

    /**
     * Receives a username and returns a new UserDetails object for
     * a user matching that username. Throws an exception if the
     * username is not in the database.
     *
     * @param username the username which we are checking in the database
     * @return UserDetails build from the user
     * @throws UsernameNotFoundException thrown if the username doesn't
     *      exist in the database
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username).orElseThrow(() ->
                new UsernameNotFoundException("User Not Found with username: " + username));
        return UserDetailsImpl.build(user);
    }

    /**
     * Receives user credentials to be registered int the system.
     * If the username is already used or the role of the new user is invalid
     * a response with status 400 is returned.
     *
     * @param user the new user to be registered into the system
     * @return an authentication response with the jwt token if the registration was valid
     */
    public boolean register(User user) {
        if (!checkIfValidForRegistration(user)) {
            return false;
        }
        if (user.getRole().equals("ROLE_STUDENT")) {
            saveNewStudent(user.getUsername(), user.getName());
        }
        if (user.getRole().equals("ROLE_COMPANY")) {
            saveNewCompany(user.getUsername(), user.getName());
        }
        userRepository.save(user);
        return true;
    }

    /**
     * Sends a request to the Student microservice to add a
     * new student to its database.
     *
     * @param username the username of the new student
     * @param name the real name of the new student
     */
    private void saveNewStudent(String username, String name) {
        HttpEntity<String> request = generateRequest(username, name);
        restTemplate.postForObject("http://STUDENT-SERVICE/student/"
                + username + "/", request, Object.class);
    }

    /**
     * Sends a request to the Company microservice to add a
     * new company to its database.
     *
     * @param username the username of the new company
     * @param name the real name of the new company
     */
    private void saveNewCompany(String username, String name) {
        HttpEntity<String> request = generateRequest(username, name);
        restTemplate.postForObject("http://COMPANY-SERVICE/company/", request, Object.class);
    }

    /**
     * Generate an HttpEntity with a json object containing
     * the username and name of the new user.
     *
     * @param username the username of the user
     * @param name the real name of the user
     * @return new HttpEntity from the given username and name.
     */
    private HttpEntity<String> generateRequest(String username, String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userJsonObject = gson
                .toJson(new RequestUser(username, name));
        return new HttpEntity<>(userJsonObject, headers);
    }

    /**
     * Receives a user and checks whether their nickname
     * is already taken or they have an invalid role.
     *
     * @param user the user to be checked
     * @return true if the role matches the pattern and the
     *         nickname is not taken
     */
    private boolean checkIfValidForRegistration(User user) {
        String role = user.getRole();
        return userRepository.findById(user.getUsername()).isEmpty() && role.startsWith("ROLE_")
                && (role.endsWith("STUDENT") || role.endsWith("COMPANY"));
    }
}
