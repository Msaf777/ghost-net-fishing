package org.saflo.ghostNetFishing.model.Entity;

import jakarta.persistence.*;
import org.saflo.ghostNetFishing.model.enums.PersonType;


@Entity
@Table(name = "PERSON")
public class Person {
    @Id
    @GeneratedValue
    @Column(name="person_id")
    private Long id;

    @Column()
    private String name;

    @Column()
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private PersonType type;

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
