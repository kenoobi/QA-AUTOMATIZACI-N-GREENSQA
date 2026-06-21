package co.greensqa.testdata.generator;

import co.greensqa.testdata.domain.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public final class TestDataGenerator {
    private static final String[] FIRST_NAMES = {"Sofía", "Mateo", "Valentina", "Samuel", "Isabella", "Nicolás", "Emma", "Daniel", "Luciana", "Tomás"};
    private static final String[] LAST_NAMES = {"Rodríguez", "Gómez", "Martínez", "López", "García", "Torres", "Ramírez", "Vargas", "Castro", "Rojas"};
    private static final String[] COMPANY_PREFIX = {"Andina", "Cóndor", "Pacífico", "Quimbaya", "Orquídea", "Nevado"};
    private static final String[] COMPANY_SUFFIX = {"Logística SAS", "Tecnología SAS", "Viajes Ltda", "Servicios SAS"};
    private static final Location[] LOCATIONS = {
        new Location("Bogotá", "Colombia", "Español"), new Location("Medellín", "Colombia", "Español"),
        new Location("Cali", "Colombia", "Español"), new Location("Cartagena", "Colombia", "Español"),
        new Location("Miami", "Estados Unidos", "Inglés"), new Location("São Paulo", "Brasil", "Portugués"),
        new Location("París", "Francia", "Francés"), new Location("Londres", "Reino Unido", "Inglés")
    };

    private final PersonFactory factory;
    private final Map<PersonType, DocumentStrategy> documentStrategies;
    private final Set<String> documents;
    private final Set<String> names;
    private final AtomicLong sequence = new AtomicLong();

    public TestDataGenerator(PersonFactory factory, Set<String> documents, Set<String> names) {
        this(factory, DocumentStrategies.defaults(), documents, names);
    }

    public TestDataGenerator(PersonFactory factory, Map<PersonType, DocumentStrategy> strategies,
                             Set<String> documents, Set<String> names) {
        this.factory = Objects.requireNonNull(factory);
        this.documentStrategies = Map.copyOf(strategies);
        this.documents = ConcurrentHashMap.newKeySet();
        this.documents.addAll(documents);
        this.names = ConcurrentHashMap.newKeySet();
        this.names.addAll(names);
    }

    public List<TestPerson> generate(int count, boolean parallel) {
        if (count < 1 || count > 100_000) throw new IllegalArgumentException("Count must be between 1 and 100000");
        IntStream indexes = IntStream.range(0, count);
        if (parallel) indexes = indexes.parallel();
        return indexes.mapToObj(this::generateOne).toList();
    }

    private TestPerson generateOne(int index) {
        RandomGenerator random = new SplittableRandom(System.nanoTime() ^ Thread.currentThread().getId() ^ index);
        long value = sequence.getAndIncrement();
        PersonType type = PersonType.values()[(int) (value % PersonType.values().length)];
        int age = switch (type) { case MINOR -> random.nextInt(11, 18); case ADULT -> random.nextInt(18, 80); case COMPANY -> random.nextInt(11, 80); };
        String[] uniqueName = uniqueName(type, value, random);
        String document = uniqueDocument(type, random);
        Location location = LOCATIONS[random.nextInt(LOCATIONS.length)];
        return factory.create(type, uniqueName[0], uniqueName[1], age, document,
                location.city(), location.country(), location.language());
    }

    private String[] uniqueName(PersonType type, long value, RandomGenerator random) {
        for (int attempt = 0; attempt < 10_000; attempt++) {
            String name;
            String lastName;
            long suffix = value + attempt;
            if (type == PersonType.COMPANY) {
                name = COMPANY_PREFIX[random.nextInt(COMPANY_PREFIX.length)] + " " + COMPANY_SUFFIX[random.nextInt(COMPANY_SUFFIX.length)] + " " + suffix;
                lastName = "";
            } else {
                name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + suffix;
                lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            }
            if (names.add(name + "\u0000" + lastName)) return new String[]{name, lastName};
        }
        throw new IllegalStateException("Could not generate a unique name");
    }

    private String uniqueDocument(PersonType type, RandomGenerator random) {
        DocumentStrategy strategy = documentStrategies.get(type);
        for (int attempt = 0; attempt < 10_000; attempt++) {
            String candidate = strategy.generate(random);
            if (documents.add(candidate)) return candidate;
        }
        throw new IllegalStateException("Could not generate a unique document");
    }

    private record Location(String city, String country, String language) {}
}
