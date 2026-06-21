package co.greensqa.testdata.service;

import co.greensqa.testdata.domain.TestPerson;
import co.greensqa.testdata.export.CsvExporter;
import co.greensqa.testdata.generator.*;
import co.greensqa.testdata.repository.PersonRepository;

import java.nio.file.Path;
import java.util.List;

public final class TestDataService {
    private final PersonRepository repository;
    private final CsvExporter exporter;

    public TestDataService(PersonRepository repository, CsvExporter exporter) {
        this.repository = repository;
        this.exporter = exporter;
    }

    public GenerationResult generate(int count, boolean parallel, Path csvPath) {
        TestDataGenerator generator = new TestDataGenerator(new PersonFactory(),
                repository.existingDocuments(), repository.existingFullNames());
        List<TestPerson> people = generator.generate(count, parallel).stream().map(repository::save).toList();
        Path csv = exporter.export(people, csvPath);
        return new GenerationResult(people, csv);
    }

    public List<TestPerson> list() { return repository.findAll(); }
    public TestPerson find(long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No record with id " + id)); }
    public boolean delete(long id) { return repository.deleteById(id); }
    public int clear() { return repository.deleteAll(); }

    public record GenerationResult(List<TestPerson> people, Path csvPath) {}
}
