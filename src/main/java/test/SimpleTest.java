package test;

import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeClass;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;

public class SimpleTest {

	public static WebDriver driver;

	@Test
	public void f() {
		// search một đoạn text
		driver.findElement(By.xpath("//input[@class='gLFyf']")).sendKeys("viblo");
		driver.findElement(By.xpath("//input[@class='gLFyf']")).sendKeys(Keys.ENTER);
		assertTrue(driver.getTitle().contains("viblo - Tìm trên Google"));
	}

	@BeforeClass
	public void beforeClass() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get("https://www.google.com/");
	}

	@AfterClass
	public void afterClass() {
		driver.close();
		driver.quit();
	}
    
}