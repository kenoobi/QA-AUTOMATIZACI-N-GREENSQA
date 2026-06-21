package co.greensqa.testdata;

import co.greensqa.testdata.domain.Individual;
import co.greensqa.testdata.repository.H2PersonRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class H2PersonRepositoryTest {
    @Test void supportsCreateReadListDeleteAndHistoricalUniqueness() {
        H2PersonRepository repository = new H2PersonRepository("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        Individual person = new Individual(null, "Ana", "Rojas", 28, "123456789", "Bogotá", "Colombia", "Español");

        repository.save(person);
        assertNotNull(person.id());
        assertEquals("Ana Rojas", repository.findById(person.id()).orElseThrow().displayName());
        assertEquals(1, repository.findAll().size());
        assertTrue(repository.existingDocuments().contains("123456789"));
        assertTrue(repository.existingFullNames().contains(H2PersonRepository.key("Ana", "Rojas")));
        assertTrue(repository.deleteById(person.id()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test void databaseEnforcesUniqueDocumentsAndNames() {
        H2PersonRepository repository = new H2PersonRepository("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        repository.save(new Individual(null, "Ana", "Rojas", 28, "123456789", "Bogotá", "Colombia", "Español"));
        assertThrows(IllegalStateException.class, () -> repository.save(
                new Individual(null, "Otra", "Persona", 30, "123456789", "Cali", "Colombia", "Español")));
        assertThrows(IllegalStateException.class, () -> repository.save(
                new Individual(null, "Ana", "Rojas", 30, "223456789", "Cali", "Colombia", "Español")));
    }
}
