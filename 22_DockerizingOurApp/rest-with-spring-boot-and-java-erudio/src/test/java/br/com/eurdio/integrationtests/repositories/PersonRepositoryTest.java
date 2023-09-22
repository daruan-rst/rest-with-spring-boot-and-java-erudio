package br.com.eurdio.integrationtests.repositories;

import br.com.eurdio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.eurdio.model.Person;
import br.com.eurdio.repository.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setUp(){
        person = new Person();
    }

    @Test
    @Order(0)
    public void testFindByName() {

        Pageable pageable = PageRequest.of(0,6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonByName("ayr", pageable).getContent().get(0);

        Assertions.assertEquals("Ayrton", person.getFirstName());
        Assertions.assertEquals("Senna", person.getLastName());
        Assertions.assertEquals("São Paulo", person.getAddress());
        Assertions.assertEquals("Male", person.getGender());
        Assertions.assertTrue(person.getEnabled());

    }

    @Test
    @Order(1)
    public void testDisablePerson() {

        repository.disablePerson(person.getId());

        Pageable pageable = PageRequest.of(0,6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonByName("ayr", pageable).getContent().get(0);

        Assertions.assertEquals("Ayrton", person.getFirstName());
        Assertions.assertEquals("Senna", person.getLastName());
        Assertions.assertEquals("São Paulo", person.getAddress());
        Assertions.assertEquals("Male", person.getGender());
        Assertions.assertFalse(person.getEnabled());

    }
}
