package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class PatientEligibilityRestAdapter implements PatientEligibilityPort {

    private final RestTemplate restTemplate;

    @Value("${services.patients.base-url}")
    private String patientsBaseUrl;

    public PatientEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean exists(String patientId) {
        String url = patientsBaseUrl +"/api/patients/"+ patientId +"/eligibility";
        try{
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        }catch(HttpClientErrorException.NotFound ex){
            throw new CrossContextValidationException("Patient not found"+ patientId);
        }catch(HttpClientErrorException ex){
            throw new CrossContextValidationException("Patient validation failed"+ patientId);
        }catch(Exception ex){
            throw new CrossContextValidationException("Patient service is unavailable");
        }

    }
}