package nl.tudelft.sem.request.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.request.valueobjects.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public abstract class RequestService {

    @Autowired
    private RestTemplate restTemplate;
    private final Gson gson = new GsonBuilder().create();

    /**
     * Sends a generated contract to the contract microservice.
     *
     * @param contract The generated contract to be sent
     * @return The contract
     */
    public Contract sendGeneratedContract(Contract contract) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String contractJsonObject = gson.toJson(contract);
        HttpEntity<String> request = new HttpEntity<>(contractJsonObject, headers);
        restTemplate.postForObject("http://CONTRACT-SERVICE/contracts/", request, Contract.class);
        return contract;
    }
}
