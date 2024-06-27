package org.saflo.ghostNetFishing.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.saflo.ghostNetFishing.model.DAO.PersonDAO;
import org.saflo.ghostNetFishing.model.Entity.Person;
import org.saflo.ghostNetFishing.model.enums.PersonType;
import org.saflo.ghostNetFishing.util.SessionUtil;

@Named
@ApplicationScoped
public class PersonService {

    private PersonDAO personDAO;

    @Inject
    public PersonService(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public PersonService() {}

    /**
     * Registers a new person and logs them in.
     * @param name the name of the person.
     * @param phoneNumber the phone number of the person.
     * @param password the password of the person.
     * @param type the type of the person (REPORTER or RECOVERER).
     */
    public void registerPerson(String name, String phoneNumber, String password, PersonType type) {
        Person person = new Person(name, phoneNumber, type, password);
        personDAO.addPerson(person);
        SessionUtil.setLoggedInPerson(person);
    }

    /**
     * Checks if a user with the given name already exists.
     * @param name the name to check.
     * @return false if the user exists, otherwise true.
     */
    public boolean isNameAvailable(String name) {
        Person existingUser = personDAO.findPersonByName(name);
        return existingUser == null;
    }

    /**
     * Checks if a user with the given phone number already exists.
     * @param phoneNumber the phone number to check.
     * @return false if the user exists, otherwise true.
     */
    public boolean isPhoneNumberAvailable(String phoneNumber) {
        Person existingUser = personDAO.findPersonByPhone(phoneNumber);
        return existingUser == null;
    }
}
