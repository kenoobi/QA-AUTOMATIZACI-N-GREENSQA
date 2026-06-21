package co.greensqa.testdata.generator;

import co.greensqa.testdata.domain.PersonType;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class DocumentStrategies {
    private DocumentStrategies() {}

    public static Map<PersonType, DocumentStrategy> defaults() {
        return Map.of(
            PersonType.COMPANY, r -> "9" + digits(r, 9),
            PersonType.MINOR, r -> Long.toString(r.nextLong(11_000_000L, 100_000_000L)),
            PersonType.ADULT, r -> {
                int length = r.nextInt(9, 12);
                return Integer.toString(r.nextInt(1, 9)) + digits(r, length - 1);
            }
        );
    }

    private static String digits(RandomGenerator random, int count) {
        StringBuilder out = new StringBuilder(count);
        for (int i = 0; i < count; i++) out.append(random.nextInt(10));
        return out.toString();
    }
}
