package com.champsoft.healthcare.patients.domain.model;

import java.util.Objects;
import java.util.UUID;

public class PatientId {

    private final String value;

    public PatientId(String id){
        this.value=id;
    }

    public static PatientId newId(){
        return new PatientId(UUID.randomUUID().toString());
    }

    public static PatientId of(String id){
        return new PatientId(id);
    }

    public String getId() {
        return value;
    }

    @Override
    public boolean equals(Object o){
        return (o instanceof PatientId other) && Objects.equals(value,other.value);
    }

    @Override
    public int hashCode(){
        return Objects.hash(value);
    }

    @Override
    public String toString(){
        return value;
    }

}
