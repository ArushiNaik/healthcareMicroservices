package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class DoctorEligibilityRestAdapter implements DoctorEligibilityPort {

    private final RestTemplate restTemplate;
    @Value("{services.doctors.base-url}")
    private String doctorBaseUrl;

    public DoctorEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean exists(String doctorId) {
        String url = doctorBaseUrl +"/api/doctors"+ doctorBaseUrl +"/eligibility";
        try{
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        }catch(HttpClientErrorException.NotFound ex){
            throw new CrossContextValidationException("Doctor not found"+ doctorId);
        }catch(HttpClientErrorException ex){
            throw new CrossContextValidationException("Doctor validation failed"+ doctorId);
        }catch(Exception ex){
            throw new CrossContextValidationException("Doctor service is unavailable");
        }

    }
    }
