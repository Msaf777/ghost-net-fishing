package org.saflo.ghostNetFishing.model;

import jakarta.persistence.*;


@Entity
@Table(name = "PERSON")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private PersonType type;

    // Konstruktoren
    public Person() {
    }

    public Person(String name, String phoneNumber, PersonType type) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
