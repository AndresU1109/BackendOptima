package com.hrks.OptimaStock.person.service;

import com.hrks.OptimaStock.person.model.Person;
import com.hrks.OptimaStock.person.repository.PersonRepository;
import com.hrks.OptimaStock.typeDocument.model.TypeDocument;
import com.hrks.OptimaStock.typeDocument.repository.TypeDocumentRepository;
import com.hrks.OptimaStock.typePerson.model.TypePerson;
import com.hrks.OptimaStock.typePerson.repository.TypePersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final TypePersonRepository typePersonRepository;

    public PersonService(PersonRepository personRepository,
            TypeDocumentRepository typeDocumentRepository,
            TypePersonRepository typePersonRepository) {
        this.personRepository = personRepository;
        this.typeDocumentRepository = typeDocumentRepository;
        this.typePersonRepository = typePersonRepository;
    }

    @Cacheable(value = "persons", key = "'all'")
    public List<Person> findAll() {
        logger.debug("Fetching all persons from database");
        return personRepository.findAll();
    }

    @Cacheable(value = "persons", key = "#id")
    public Optional<Person> findById(Integer id) {
        logger.debug("Fetching person with id: {}", id);
        return personRepository.findById(id);
    }

    @CachePut(value = "persons", key = "#result.id")
    @CacheEvict(value = "persons", key = "'all'")
    public Person save(Person person) {
        logger.info("Saving person: {} {}", person.getFirstName(), person.getLastName());

        // Fetch and validate TypeDocument
        if (person.getTypeDocument() != null && person.getTypeDocument().getIdTypeDocument() != null) {
            TypeDocument typeDocument = typeDocumentRepository.findById(person.getTypeDocument().getIdTypeDocument())
                    .orElseThrow(() -> new RuntimeException("TypeDocument with id " +
                            person.getTypeDocument().getIdTypeDocument() + " not found"));
            person.setTypeDocument(typeDocument);
        }

        // Fetch and validate TypePerson
        if (person.getTypePerson() != null && person.getTypePerson().getIdTypePerson() != null) {
            TypePerson typePerson = typePersonRepository.findById(person.getTypePerson().getIdTypePerson())
                    .orElseThrow(() -> new RuntimeException("TypePerson with id " +
                            person.getTypePerson().getIdTypePerson() + " not found"));
            person.setTypePerson(typePerson);
        }

        Person savedPerson = personRepository.save(person);
        logger.info("Person saved successfully with id: {}", savedPerson.getId());
        return savedPerson;
    }

    @CacheEvict(value = "persons", allEntries = true)
    public void delete(Integer id) {
        logger.info("Deleting person with id: {}", id);
        personRepository.deleteById(id);
        logger.info("Person deleted successfully");
    }
}
