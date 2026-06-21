package co.greensqa.testdata.export;

import co.greensqa.testdata.domain.TestPerson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class CsvExporter {
    public Path export(List<TestPerson> people, Path destination) {
        try {
            Path parent = destination.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            StringBuilder csv = new StringBuilder("id,tipo,nombre,apellido,edad,documento,ciudad,pais,idioma\n");
            for (TestPerson p : people) {
                csv.append(p.id() == null ? "" : p.id()).append(',')
                    .append(p.type()).append(',').append(escape(p.name())).append(',')
                    .append(escape(p.lastName())).append(',').append(p.age()).append(',')
                    .append(escape(p.document())).append(',').append(escape(p.city())).append(',')
                    .append(escape(p.country())).append(',').append(escape(p.language())).append('\n');
            }
            Files.writeString(destination, csv, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return destination.toAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot export CSV", e);
        }
    }

    private static String escape(String value) {
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}
