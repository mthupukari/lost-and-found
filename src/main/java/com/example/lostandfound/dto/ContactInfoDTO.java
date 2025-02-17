package com.example.lostandfound.dto;

public class ContactInfoDTO {
    
    private String email;

    public ContactInfoDTO() {
    }
    
    public ContactInfoDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
