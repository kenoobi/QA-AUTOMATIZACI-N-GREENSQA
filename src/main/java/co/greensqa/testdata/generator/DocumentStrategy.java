package co.greensqa.testdata.generator;

import java.util.random.RandomGenerator;

@FunctionalInterface
public interface DocumentStrategy {
    String generate(RandomGenerator random);
}
