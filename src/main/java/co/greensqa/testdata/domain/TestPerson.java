package co.greensqa.testdata.domain;

import java.util.Objects;

/** Abstract domain model: common invariants are encapsulated here. */
public abstract class TestPerson {
    private Long id;
    private final String name;
    private final String lastName;
    private final int age;
    private final String document;
    private final String city;
    private final String country;
    private final String language;

    protected TestPerson(Long id, String name, String lastName, int age, String document,
                         String city, String country, String language) {
        this.id = id;
        this.name = requireText(name, "name");
        this.lastName = Objects.requireNonNull(lastName, "lastName");
        if (age <= 10 || age >= 80) throw new IllegalArgumentException("Age must be between 11 and 79");
        this.age = age;
        this.document = requireText(document, "document");
        this.city = requireText(city, "city");
        this.country = requireText(country, "country");
        this.language = requireText(language, "language");
        if (!"Colombia".equalsIgnoreCase(country) && "Español".equalsIgnoreCase(language)) {
            throw new IllegalArgumentException("Residents outside Colombia cannot use Spanish");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value;
    }

    public abstract PersonType type();
    public String displayName() { return (name + " " + lastName).trim(); }
    public Long id() { return id; }
    public void assignId(long id) { if (this.id != null) throw new IllegalStateException("ID already assigned"); this.id = id; }
    public String name() { return name; }
    public String lastName() { return lastName; }
    public int age() { return age; }
    public String document() { return document; }
    public String city() { return city; }
    public String country() { return country; }
    public String language() { return language; }
}
