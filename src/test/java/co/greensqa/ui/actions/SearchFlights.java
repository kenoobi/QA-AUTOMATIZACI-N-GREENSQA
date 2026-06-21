package co.greensqa.ui.actions;

import co.greensqa.ui.pages.LatamHomePage;
import net.serenitybdd.annotations.Step;

public class SearchFlights {
    LatamHomePage homePage;

    @Step("Abrir el buscador de LATAM")
    public void openLatam() {
        homePage.open();
        homePage.acceptCookiesIfPresent();
    }

    @Step("Buscar un vuelo {0} de {1} a {2}")
    public void fromTo(String tripType, String origin, String destination, int departureDays, Integer returnDays) {
        homePage.selectTripType(tripType);
        homePage.selectRoute(origin, destination);
        homePage.selectDates(departureDays, returnDays);
        homePage.search();
    }

    @Step("Comprobar que LATAM muestra ofertas")
    public boolean foundResults() { return homePage.resultsAreDisplayed(); }
}
