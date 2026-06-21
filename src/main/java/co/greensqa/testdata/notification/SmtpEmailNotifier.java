package co.greensqa.testdata.notification;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.nio.file.Path;
import java.util.Properties;

public final class SmtpEmailNotifier implements ExportNotifier {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String sender;

    public SmtpEmailNotifier(String host, int port, String username, String password, String sender) {
        this.host = host; this.port = port; this.username = username; this.password = password; this.sender = sender;
    }

    public static SmtpEmailNotifier fromEnvironment() {
        return new SmtpEmailNotifier(required("SMTP_HOST"), Integer.parseInt(System.getenv().getOrDefault("SMTP_PORT", "587")),
                required("SMTP_USER"), required("SMTP_PASSWORD"), System.getenv().getOrDefault("SMTP_FROM", required("SMTP_USER")));
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing environment variable " + name);
        return value;
    }

    @Override public void notify(String recipient, Path attachment) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", Integer.toString(port));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Datos de prueba GreenSQA - LATAM", "UTF-8");
            MimeBodyPart body = new MimeBodyPart();
            body.setText("Se adjunta el archivo CSV con los datos ficticios generados.", "UTF-8");
            MimeBodyPart file = new MimeBodyPart();
            file.setDataHandler(new DataHandler(new FileDataSource(attachment.toFile())));
            file.setFileName(attachment.getFileName().toString());
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(body); multipart.addBodyPart(file);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Cannot send CSV by email", e);
        }
    }
}
