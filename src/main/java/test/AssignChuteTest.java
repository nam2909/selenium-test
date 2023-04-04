package test;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import entities.EsbAsCaseContentDetail;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.hibernate.Session;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.HibernateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;
import static org.junit.Assert.*;

/**
 * @author Nam Nguyen <namnguyen@fortna.com>
 * @since 4/4/2023.
 */
public class AssignChuteTest {
    public static WebDriver driver;

    public static WebDriverWait wait;

    public TelnetSample telnet = new TelnetSample("10.240.44.11", 19901);


    @Test
    public void assignChuteTest() throws InterruptedException {
        driver.get("http://10.240.44.11:17033/#/unitsorter/?deviceId=SCR001&scannerHost=10.240.44.11&scannerPort=19901&serviceId=OPTIMUS_UNIT_SORTER&serviceName=Optimus%20unit%20sorter");

        for (int i = 2; i < 6; i++) {
            driver.findElement(By.xpath("//button[@value='START_ASSIGN_CONTAINER_TO_CHUTE']")).click();
            Thread.sleep(1000);
            telnet.sendCommand(String.format("%04d", i));
            Thread.sleep(1000);
            telnet.sendCommand(String.valueOf(1000000000+i));
            Thread.sleep(1000);
            telnet.sendCommand(String.format("%04d", i));
            Thread.sleep(1000);
            driver.findElement(By.xpath("//button[@value='COMPLETE_PROCESS']")).click();
            Thread.sleep(500);
        }
    }


    @BeforeClass
    public void beforeClass() {
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver,ofSeconds(30));
    }

    @AfterClass
    public void afterClass() throws InterruptedException {
//        driver.close();
//        driver.quit();
    }
}
