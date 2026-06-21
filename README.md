# GreenSQA - POC Shift Right para LATAM

Solución completa de la prueba técnica: generador de datos ficticios, persistencia H2, CSV, gestión histórica, envío opcional por correo y tres casos UI con Serenity BDD + Cucumber.

## Requisitos

- JDK 17 o superior
- Maven 3.9 o superior
- Google Chrome para la suite UI

## Ejecución rápida

```bash
mvn test
mvn exec:java -Dexec.args="generate 20 --parallel --out output/personas.csv"
mvn exec:java -Dexec.args="list"
```

El primer comando ejecuta las pruebas unitarias. El segundo genera exactamente 20 registros en paralelo, los persiste en `data/test-data.mv.db` y crea el CSV solicitado.

Comandos de gestión de ejecuciones anteriores:

```bash
mvn exec:java -Dexec.args="find 1"
mvn exec:java -Dexec.args="delete 1"
mvn exec:java -Dexec.args="clear"
```

## Automatización UI

Los tres casos diseñados están en `src/test/resources/features/flight_search.feature`:

1. Vuelo nacional de ida y vuelta.
2. Vuelo internacional de solo ida.
3. Segundo destino internacional de ida y vuelta.

Cada escenario genera y persiste un viajero, usa su ciudad para determinar el aeropuerto de origen y adjunta el dato al reporte de Serenity.

```bash
mvn clean verify -Pui
```

El reporte queda en `target/site/serenity/index.html`. La suite corre tres escenarios en paralelo. Para depurar con ventana visible, retire `--headless=new` de `src/test/resources/serenity.conf`.

> LATAM es un sitio de terceros que cambia con frecuencia y puede aplicar CAPTCHA, geolocalización o límites de tráfico. Los selectores semánticos y sus alternativas están aislados en `LatamHomePage`.

## Envío del CSV por correo (bonus)

Configure un servidor SMTP y agregue `--email`:

```bash
export SMTP_HOST=smtp.example.com SMTP_PORT=587
export SMTP_USER=usuario SMTP_PASSWORD=secreto SMTP_FROM=qa@example.com
mvn exec:java -Dexec.args="generate 10 --email destino@example.com"
```

## Diseño

- **Encapsulamiento:** invariantes y campos privados en `TestPerson`.
- **Abstracción:** `TestPerson`, `PersonRepository`, `DocumentStrategy` y `ExportNotifier`.
- **Herencia:** `Individual` y `Company` extienden `TestPerson`.
- **Polimorfismo:** `type()` y `displayName()` tienen comportamiento específico por subtipo.
- **Patrones:** Strategy para documentos, Factory para personas y Repository para persistencia.
- **SOLID:** responsabilidades pequeñas (SRP), extensiones mediante interfaces (OCP/DIP), subtipos válidos (LSP) e interfaces acotadas (ISP).

La base de datos conserva restricciones `UNIQUE` para documento y nombre/apellido, incluso entre ejecuciones. Las estructuras concurrentes evitan duplicados durante generación paralela.

## Publicación

```bash
git init
git add .
git commit -m "feat: implement GreenSQA LATAM automation POC"
git branch -M main
git remote add origin <URL_DEL_REPOSITORIO_PUBLICO>
git push -u origin main
```

No se incluyen credenciales ni archivos generados en Git gracias a `.gitignore`.
