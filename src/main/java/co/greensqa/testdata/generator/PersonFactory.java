package co.greensqa.testdata.generator;

import co.greensqa.testdata.domain.*;

public final class PersonFactory {
    public TestPerson create(PersonType type, String name, String lastName, int age, String document,
                             String city, String country, String language) {
        if (type == PersonType.MINOR && age >= 18) throw new IllegalArgumentException("A minor must be under 18");
        if (type == PersonType.ADULT && age < 18) throw new IllegalArgumentException("An adult must be at least 18");
        return switch (type) {
            case COMPANY -> new Company(null, name, age, document, city, country, language);
            case MINOR, ADULT -> new Individual(null, name, lastName, age, document, city, country, language);
        };
    }
}
