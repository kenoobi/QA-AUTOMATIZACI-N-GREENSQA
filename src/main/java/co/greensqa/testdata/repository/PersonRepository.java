package co.greensqa.testdata.repository;

import co.greensqa.testdata.domain.TestPerson;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonRepository {
    TestPerson save(TestPerson person);
    List<TestPerson> findAll();
    Optional<TestPerson> findById(long id);
    boolean deleteById(long id);
    int deleteAll();
    Set<String> existingDocuments();
    Set<String> existingFullNames();
}
