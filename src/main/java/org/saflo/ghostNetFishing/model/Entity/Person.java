package org.saflo.ghostNetFishing.model.Entity;

import jakarta.persistence.*;
import org.saflo.ghostNetFishing.model.enums.PersonType;


@Entity
@Table(name = "PERSON")
public class Person {
    @Id
    @GeneratedValue
    @Column(name="person_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private PersonType type;

    @Column(nullable = false)
    private String password;

    public Person() {
    }

    public Person(String name, String phoneNumber, PersonType type, String password) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.password = password;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
