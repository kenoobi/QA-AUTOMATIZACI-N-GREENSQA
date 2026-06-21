package co.greensqa.testdata.domain;

public final class Company extends TestPerson {
    public Company(Long id, String name, int age, String document, String city, String country, String language) {
        super(id, name, "", age, document, city, country, language);
        if (!document.matches("9\\d+")) throw new IllegalArgumentException("A company document must be numeric and start with 9");
    }

    @Override public PersonType type() { return PersonType.COMPANY; }
    @Override public String displayName() { return name(); }
}
