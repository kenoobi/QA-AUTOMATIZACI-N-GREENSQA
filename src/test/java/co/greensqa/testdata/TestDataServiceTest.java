package co.greensqa.testdata;

import co.greensqa.testdata.export.CsvExporter;
import co.greensqa.testdata.repository.H2PersonRepository;
import co.greensqa.testdata.service.TestDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TestDataServiceTest {
    @TempDir Path tempDir;

    @Test void persistsAndExportsTheRequestedNumberOfRows() throws Exception {
        TestDataService service = new TestDataService(
                new H2PersonRepository("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1"), new CsvExporter());
        Path csv = tempDir.resolve("people.csv");

        var result = service.generate(12, true, csv);

        assertEquals(12, result.people().size());
        assertEquals(12, service.list().size());
        assertEquals(13, Files.readAllLines(csv).size());
        assertEquals("id,tipo,nombre,apellido,edad,documento,ciudad,pais,idioma", Files.readAllLines(csv).get(0));
        assertEquals(12, service.clear());
    }
}
