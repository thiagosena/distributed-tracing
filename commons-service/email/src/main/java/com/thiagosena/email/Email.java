package com.thiagosena.email;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Email {

    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String content;

    public Email() {
    }

    public Email(String email, String content) {
        this.email = email;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }
}
