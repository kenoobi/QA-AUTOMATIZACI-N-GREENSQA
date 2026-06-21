package co.greensqa;

import co.greensqa.testdata.domain.TestPerson;
import co.greensqa.testdata.export.CsvExporter;
import co.greensqa.testdata.notification.SmtpEmailNotifier;
import co.greensqa.testdata.repository.H2PersonRepository;
import co.greensqa.testdata.service.TestDataService;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

public final class App {
    private static final String DB_URL = System.getProperty("db.url", "jdbc:h2:file:./data/test-data");

    public static void main(String[] args) {
        ensureDataDirectory();
        TestDataService service = new TestDataService(new H2PersonRepository(DB_URL), new CsvExporter());
        if (args.length == 0) { usage(); return; }
        try {
            switch (args[0]) {
                case "generate" -> generate(service, args);
                case "list" -> service.list().forEach(App::print);
                case "find" -> print(service.find(longArg(args, 1, "id")));
                case "delete" -> System.out.println(service.delete(longArg(args, 1, "id")) ? "Deleted" : "Not found");
                case "clear" -> System.out.println("Deleted records: " + service.clear());
                default -> usage();
            }
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void ensureDataDirectory() {
        if (!DB_URL.startsWith("jdbc:h2:file:./data/")) return;
        try { Files.createDirectories(Path.of("data")); }
        catch (IOException e) { throw new IllegalStateException("Cannot create data directory", e); }
    }

    private static void generate(TestDataService service, String[] args) {
        int count = Math.toIntExact(longArg(args, 1, "count"));
        boolean parallel = contains(args, "--parallel");
        Path path = Path.of(option(args, "--out", "output/test-data.csv"));
        var result = service.generate(count, parallel, path);
        System.out.printf("Generated and stored: %d%nCSV: %s%n", result.people().size(), result.csvPath());
        String recipient = option(args, "--email", null);
        if (recipient != null) {
            SmtpEmailNotifier.fromEnvironment().notify(recipient, result.csvPath());
            System.out.println("Email sent to: " + recipient);
        }
    }

    private static long longArg(String[] args, int index, String name) {
        if (args.length <= index) throw new IllegalArgumentException("Missing " + name);
        return Long.parseLong(args[index]);
    }

    private static boolean contains(String[] args, String value) {
        for (String arg : args) if (value.equals(arg)) return true;
        return false;
    }

    private static String option(String[] args, String name, String fallback) {
        for (int i = 0; i < args.length - 1; i++) if (name.equals(args[i])) return args[i + 1];
        return fallback;
    }

    private static void print(TestPerson p) {
        System.out.printf("%d | %s | %s | %d | %s | %s, %s | %s%n",
                p.id(), p.type(), p.displayName(), p.age(), p.document(), p.city(), p.country(), p.language());
    }

    private static void usage() {
        System.out.println("""
            Usage:
              generate <count> [--parallel] [--out <file.csv>] [--email <address>]
              list
              find <id>
              delete <id>
              clear
            """);
    }
}
