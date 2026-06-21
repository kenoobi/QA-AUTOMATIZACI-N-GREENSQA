package co.greensqa.testdata.notification;

import java.nio.file.Path;

@FunctionalInterface
public interface ExportNotifier {
    void notify(String recipient, Path attachment);
}
