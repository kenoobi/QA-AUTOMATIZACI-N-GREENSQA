package co.greensqa.testdata;

import co.greensqa.testdata.domain.*;
import co.greensqa.testdata.generator.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestDataGeneratorTest {
    @Test void generatesAllTypesAndRespectsEveryBusinessRuleInParallel() {
        List<TestPerson> people = new TestDataGenerator(new PersonFactory(), Set.of(), Set.of()).generate(600, true);

        assertEquals(600, people.size());
        assertEquals(600, people.stream().map(TestPerson::document).distinct().count());
        assertEquals(600, people.stream().map(p -> p.name() + "|" + p.lastName()).distinct().count());
        assertEquals(EnumSet.allOf(PersonType.class), people.stream().map(TestPerson::type).collect(java.util.stream.Collectors.toSet()));

        people.forEach(p -> {
            assertTrue(p.age() > 10 && p.age() < 80);
            if (p.type() == PersonType.COMPANY) {
                assertTrue(p.lastName().isBlank());
                assertTrue(p.document().startsWith("9"));
            } else if (p.type() == PersonType.MINOR) {
                assertTrue(Long.parseLong(p.document()) >= 11_000_000L);
            } else {
                assertTrue(p.document().length() > 8 && p.document().length() < 12);
            }
            if (!"Colombia".equals(p.country())) assertNotEquals("Español", p.language());
        });
    }

    @Test void rejectsInvalidCounts() {
        TestDataGenerator generator = new TestDataGenerator(new PersonFactory(), Set.of(), Set.of());
        assertThrows(IllegalArgumentException.class, () -> generator.generate(0, false));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(100_001, false));
    }
}
