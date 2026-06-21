package co.greensqa.ui.pages;

import net.serenitybdd.annotations.DefaultUrl;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@DefaultUrl("https://www.latamairlines.com/co/es")
public class LatamHomePage extends PageObject {
    private static final Duration WAIT = Duration.ofSeconds(20);

    public void acceptCookiesIfPresent() {
        clickFirstVisible(false,
            By.xpath("//button[contains(.,'Aceptar') or contains(.,'Entendido')]") ,
            By.id("cookies-politics-button"));
    }

    public void selectTripType(String tripType) {
        clickFirstVisible(true,
            By.xpath("//button[contains(normalize-space(.),'" + tripType + "')]") ,
            By.xpath("//*[@role='tab' and contains(normalize-space(.),'" + tripType + "')]") ,
            By.xpath("//*[self::label or self::span][contains(normalize-space(.),'" + tripType + "')]") );
    }

    public void selectRoute(String origin, String destination) {
        fillAirport(origin, true);
        fillAirport(destination, false);
    }

    private void fillAirport(String value, boolean origin) {
        WebElement input = firstVisible(true,
            By.cssSelector(origin ? "input[placeholder*='origen' i]" : "input[placeholder*='destino' i]"),
            By.cssSelector(origin ? "input[aria-label*='origen' i]" : "input[aria-label*='destino' i]"),
            By.xpath(origin
                ? "//label[contains(.,'Desde')]/following::input[1]"
                : "//label[contains(.,'Hacia')]/following::input[1]"));
        input.click(); input.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
        WebDriverWait wait = new WebDriverWait(getDriver(), WAIT);
        WebElement option = wait.until(driver -> driver.findElements(By.cssSelector("[role='option']")).stream()
                .filter(WebElement::isDisplayed).findFirst().orElse(null));
        option.click();
    }

    public void selectDates(int departureOffset, Integer returnOffset) {
        selectDate(LocalDate.now().plusDays(departureOffset), true);
        if (returnOffset != null) selectDate(LocalDate.now().plusDays(returnOffset), false);
    }

    private void selectDate(LocalDate date, boolean departure) {
        List<By> direct = List.of(
            By.cssSelector("[data-date='" + date + "']"),
            By.cssSelector("[data-testid*='" + date + "']"),
            By.cssSelector("button[aria-label*='" + date.getDayOfMonth() + "']")
        );
        for (By by : direct) if (clickFirstVisible(false, by)) return;

        clickFirstVisible(true,
            By.xpath(departure
                ? "//button[contains(.,'Fecha de ida') or contains(.,'Ida')]"
                : "//button[contains(.,'Fecha de vuelta') or contains(.,'Vuelta')]"),
            By.cssSelector(departure ? "[data-testid*='departure']" : "[data-testid*='return']"));
        if (direct.stream().anyMatch(by -> clickFirstVisible(false, by))) return;
        throw new NoSuchElementException("No selectable date found for " + date);
    }

    public void search() {
        clickFirstVisible(true,
            By.xpath("//button[normalize-space()='Buscar' or contains(.,'Buscar vuelos')]") ,
            By.cssSelector("button[data-testid*='search']"));
    }

    public boolean resultsAreDisplayed() {
        new WebDriverWait(getDriver(), Duration.ofSeconds(45)).until(driver ->
            driver.getCurrentUrl().contains("booking") || driver.getCurrentUrl().contains("offers") ||
            visibleText("Selecciona tu vuelo", "Elige tu vuelo", "Vuelos disponibles", "No encontramos vuelos"));
        return getDriver().getCurrentUrl().contains("booking") || getDriver().getCurrentUrl().contains("offers") ||
                visibleText("Selecciona tu vuelo", "Elige tu vuelo", "Vuelos disponibles");
    }

    private boolean visibleText(String... texts) {
        return Arrays.stream(texts).anyMatch(text -> getDriver().findElements(By.xpath("//*[contains(normalize-space(.),'" + text + "')]")).stream()
                .anyMatch(WebElement::isDisplayed));
    }

    private boolean clickFirstVisible(boolean required, By... locators) {
        WebElement element = firstVisible(required, locators);
        if (element == null) return false;
        try { element.click(); } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click()", element);
        }
        return true;
    }

    private WebElement firstVisible(boolean required, By... locators) {
        WebDriverWait wait = new WebDriverWait(getDriver(), required ? WAIT : Duration.ofMillis(400));
        try {
            return wait.until(driver -> Arrays.stream(locators)
                    .flatMap(by -> driver.findElements(by).stream())
                    .filter(WebElement::isDisplayed).findFirst().orElse(null));
        } catch (TimeoutException e) {
            if (required) throw new NoSuchElementException("None of the semantic locators was visible: " + Arrays.toString(locators));
            return null;
        }
    }
}
