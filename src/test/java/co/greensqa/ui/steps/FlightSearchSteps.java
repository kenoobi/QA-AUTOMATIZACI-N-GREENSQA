package co.greensqa.ui.steps;

import co.greensqa.testdata.domain.TestPerson;
import co.greensqa.testdata.export.CsvExporter;
import co.greensqa.testdata.repository.H2PersonRepository;
import co.greensqa.testdata.service.TestDataService;
import co.greensqa.ui.actions.SearchFlights;
import io.cucumber.java.en.*;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.core.Serenity;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlightSearchSteps {
    private static final Map<String, String> AIRPORTS = Map.of(
        "Bogotá", "BOG", "Medellín", "MDE", "Cali", "CLO", "Cartagena", "CTG",
        "Miami", "MIA", "São Paulo", "GRU", "París", "CDG", "Londres", "LHR");

    @Steps SearchFlights searchFlights;
    private TestPerson traveler;

    @Given("un viajero generado y persistido como dato de entrada")
    public void generatedTraveler() {
        String db = "jdbc:h2:mem:ui_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1";
        TestDataService service = new TestDataService(new H2PersonRepository(db), new CsvExporter());
        traveler = service.generate(1, false, Path.of("target/generated-ui-data/traveler.csv")).people().get(0);
        Serenity.recordReportData().withTitle("Dato ficticio utilizado")
                .andContents(traveler.displayName() + " | " + traveler.document() + " | " + traveler.city());
    }

    @And("el viajero abre el buscador de LATAM")
    public void openSearch() { searchFlights.openLatam(); }

    @When("busca un vuelo {string} hacia {string} para dentro de {int} días con regreso en {int} días")
    public void roundTrip(String tripType, String destination, int departureDays, int returnDays) {
        String origin = AIRPORTS.getOrDefault(traveler.city(), "BOG");
        if (origin.equals(destination)) origin = "BOG".equals(destination) ? "MDE" : "BOG";
        searchFlights.fromTo(tripType, origin, destination, departureDays, returnDays);
    }

    @When("busca un vuelo {string} hacia {string} para dentro de {int} días")
    public void oneWay(String tripType, String destination, int departureDays) {
        String origin = AIRPORTS.getOrDefault(traveler.city(), "BOG");
        if (origin.equals(destination)) origin = "BOG";
        searchFlights.fromTo(tripType, origin, destination, departureDays, null);
    }

    @Then("se presentan los resultados de vuelos")
    public void results() { assertTrue(searchFlights.foundResults(), "LATAM did not show flight offers"); }
}
