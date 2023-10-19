package org.simonscode;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.By.xpath;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "./geckodriver");

        WebDriver driver = new FirefoxDriver(new FirefoxOptions()
                .setBinary("/sbin/firefox-developer-edition"));

        PrintWriter pw = new PrintWriter("./out.log");
        PrintWriter deps = new PrintWriter("./deps.log");

        List<String> mods = Files.readAllLines(new File("./mods.txt").toPath());

        Duration timeout = Duration.ofSeconds(30);
        for (String modURL : mods) {
            String modname = modURL.substring(modURL.lastIndexOf("/") + 1);
            try {
                driver.get(modURL + "/files/all?filter-game-version=1738749986%3A73250");

                WebElement element = new WebDriverWait(driver, timeout)
                        .until(elementToBeClickable(xpath("/html/body/div[1]/main/div[1]/div[2]/section/div/div/div/section/div[2]/div/table/tbody/tr[1]/td[7]/div/a[1]")));
                element.click();

                Thread.sleep(7000);

                driver.navigate().to(modURL + "/relations/dependencies");

                element = new WebDriverWait(driver, timeout)
                        .until(elementToBeClickable(xpath("/html/body/div[1]/main/div[1]/div[2]/div/div/div[3]/ul")));

                List<WebElement> li = element.findElements(xpath("li"));
                for (WebElement webElement : li) {
                    System.out.println(webElement.getText());
                    String text = webElement.findElement(xpath("div[2]/div[1]/a[1]/h3")).getText();
                    System.out.println(text);
                    deps.println(modname + ":" + text);
                }
            } catch (TimeoutException e) {
                e.printStackTrace();
                pw.println(modURL);
            }
        }
        pw.close();
        deps.close();
        driver.quit();
    }
}
