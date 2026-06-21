package co.greensqa.testdata.domain;

public final class Individual extends TestPerson {
    public Individual(Long id, String name, String lastName, int age, String document,
                      String city, String country, String language) {
        super(id, name, lastName, age, document, city, country, language);
        if (lastName.isBlank()) throw new IllegalArgumentException("An individual requires a last name");
        if (!document.matches("\\d+")) throw new IllegalArgumentException("An individual document must be numeric");
        if (age < 18 && Long.parseLong(document) < 11_000_000L) {
            throw new IllegalArgumentException("A minor document must start at 11000000");
        }
        if (age >= 18 && (document.length() <= 8 || document.length() >= 12)) {
            throw new IllegalArgumentException("An adult document must contain 9 to 11 digits");
        }
    }

    @Override public PersonType type() { return age() < 18 ? PersonType.MINOR : PersonType.ADULT; }
}
