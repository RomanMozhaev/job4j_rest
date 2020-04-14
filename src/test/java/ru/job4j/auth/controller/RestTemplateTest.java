package ru.job4j.auth.controller;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Person;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests of REST Templates.
 * Run AuthApp before tests.
 */
public class RestTemplateTest {

    private static final String API = "http://localhost:8080/person/";
    private static final String API_ID = "http://localhost:8080/person/{id}";

    @Test
    public void whenGetAllPersonsThenList() {
        RestTemplate rest = new RestTemplate();
        Person person = new Person();
        person.setLogin("test");
        person.setPassword("pass");
        Person entity = rest.postForObject(API, person, Person.class);
        List<Person> persons;
        persons = rest.exchange(
                API,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Person>>() {}
        ).getBody();
        assert persons != null;
        assertTrue(persons.size() > 0);
    }

    @Test
    public void whenCreatePersonThenEntityIdMoreThanZero() {
        RestTemplate rest = new RestTemplate();
        Person person = new Person();
        person.setLogin("test");
        person.setPassword("pass");
        Person entity = rest.postForObject(API, person, Person.class);
        assert entity != null;
        assertTrue(entity.getId() > 0);
    }

    @Test
    public void whenCreatePersonAndGetPersonThenLoginsAreEqual() {
        RestTemplate rest = new RestTemplate();
        Person person = new Person();
        person.setLogin("test1");
        person.setPassword("pass1");
        Person entity = rest.postForObject(API, person, Person.class);
        Person remote = rest.getForObject(API_ID, Person.class, entity.getId());
        assert remote != null;
        assertEquals(remote.getLogin(), entity.getLogin());
    }

    @Test
    public void whenUpdatePersonThenNewLogin() {
        RestTemplate rest = new RestTemplate();
        Person person = new Person();
        person.setLogin("test2");
        person.setPassword("pass2");
        Person entity = rest.postForObject(API, person, Person.class);
        String newName = "testModified";
        entity.setLogin(newName);
        rest.put(API, entity);
        Person remote = rest.getForObject(API_ID, Person.class, entity.getId());
        assert remote != null;
        assertEquals(remote.getLogin(), newName);
    }

    @Test
    public void whenDeletePersonThenStatusNOT_FOUND() {
        RestTemplate rest = new RestTemplate();
        Person person = new Person();
        person.setLogin("test3");
        person.setPassword("pass3");
        Person entity = rest.postForObject(API, person, Person.class);
        rest.delete(API_ID, entity.getId());
        HttpStatus statusCode = HttpStatus.FOUND;
        try {
            rest.getForObject(API_ID, Person.class, entity.getId());
        } catch (final HttpClientErrorException e) {
            statusCode = e.getStatusCode();
        }
        assertSame(statusCode, HttpStatus.NOT_FOUND);
    }



}
