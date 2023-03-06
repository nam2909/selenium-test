package test;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import entities.EsbAsCaseContentDetail;
import org.hibernate.Session;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.BeforeClass;

import static java.time.Duration.ofSeconds;
import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import utils.HibernateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimpleTest {

	public static WebDriver driver;

	public static WebDriverWait wait;

	public static ArrayList<String> poolBin = new ArrayList();

	@Test
	public void gtpDecanting() throws InterruptedException {
		List<String> listFunction = Arrays.asList("[1] GTP DECANTING", "[2] GTP CYCLE COUNTING", "[3] GTP BIN TRANSFER");
		driver.get("http://172.22.7.58:7010/#/gtpws/login?wid=GTP-02");
		//
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys("3-all");
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys("1");
		driver.findElement(By.xpath("//div[@class='login']")).click();
		Thread.sleep(3000);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(
				By.xpath("//button[@class='btn function-name btn-confirm btn btn--raised']"))));
		for(WebElement btn: driver.findElements(By.xpath("//button[@class='btn function-name btn-confirm btn btn--raised']"))){
			String menuName = btn.getDomProperty("innerText");
			System.out.println("Fuction name: "+menuName);
			assertTrue(listFunction.stream().anyMatch(o -> o.equals(menuName)));
			if (menuName.equals(listFunction.get(0))) {
				btn.click();
				wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//p[@class='qty-ordered-label']"))));
				assertEquals("Quantity", driver.findElement(By.xpath("//p[@class='qty-ordered-label']"))
						.getDomProperty("innerText"));
				setupBin();
				Thread.sleep(5000);
				break;
			}
		}

		insertEsbContentDetail();
		Thread.sleep(5000);
	}

	public void setupBin() throws InterruptedException {
		driver.findElement(By.xpath("//button[@class='btn btn-menu']")).click();
		Thread.sleep(500);
		driver.findElement(By.xpath("//p[@title='Bin setup mode']")).click();
		Thread.sleep(500);
		for(String bin: poolBin) {
			System.out.println("Bin: "+bin);
			Thread.sleep(2000);
			driver.findElement(By.xpath("//button[@class='btn btn-menu']")).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//p[@title='Key-in Barcode']")).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//input[@class='barcode']")).sendKeys(bin);
			Thread.sleep(200);
			driver.findElement(By.xpath("//div[@class='dialog-modal-footer']//button[@class='btn btn btn-confirm ml-3'][. = 'Submit']")).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//div[@class = 'custom-select select w-200']")).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//div[@class = 'items']/div[@index='0']")).click();
			Thread.sleep(500);
			driver.findElement(By.xpath("//div[@class = 'custom-btn custom-btn-primary btn-end-bin']")).click();
		}
	}

	public void insertEsbContentDetail() {
		try (Session session = HibernateUtils.getSessionFactory().openSession();) {
			// Begin a unit of work
			session.beginTransaction();

			// Insert user
			for (int i = 1; i < 100; i++) {
				EsbAsCaseContentDetail esbAsCaseContentDetail = new EsbAsCaseContentDetail();
				esbAsCaseContentDetail.setCommand("ADD");
				esbAsCaseContentDetail.setCompany("fortna");
				esbAsCaseContentDetail.setHeaderId(3000L + i);
				esbAsCaseContentDetail.setItemBarCode("SKU110");
				esbAsCaseContentDetail.setLpnNumber("T" + (78030013 + i));
				esbAsCaseContentDetail.setLpnType("Case");
				esbAsCaseContentDetail.setQuantity(10L);
				Long id = (Long) session.save(esbAsCaseContentDetail);
				System.out.println("esbAsCaseContentDetail id = " + id);

				// Get user by id
				EsbAsCaseContentDetail savedEsbContent = session.find(EsbAsCaseContentDetail.class, id);
				System.out.println("savedEsbContent: " + savedEsbContent);
				assertNotNull(savedEsbContent);
			}

			// Commit the current resource transaction, writing any unflushed changes to the database.
			session.getTransaction().commit();
		}

		for (int i = 1; i < 100; i++) {
			JtwigTemplate template = JtwigTemplate.classpathTemplate("msg/case_content.json");

			JtwigModel model = JtwigModel.newModel()
					.with("headerId", 3000 + i);

			String postApi = "http://admin:admin@amqserver:8161/api/message/ESB.CDS.CASECONTENT?type=queue";

			try {
				System.out.println(Unirest.post(postApi)
						.body(template.render(model))
						.asString());
			} catch (UnirestException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@BeforeClass
	public void beforeClass() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver,ofSeconds(30));

		// pool bin
		int binNumber = 1700001;
		for(int i = 0; i < 100; i++) {
			int binNumberNew = binNumber + i;
			poolBin.add(binNumberNew + "BK");
		}
	}

	@AfterClass
	public void afterClass() throws InterruptedException {
		if (driver.getTitle().equalsIgnoreCase(("Fortna - Function List"))) {
			driver.findElement(By.xpath("//span[@class='login-name']")).click();
			Thread.sleep(1000);
			driver.findElement(By.xpath("//div[@class='list__tile list__tile--link']")).click();
		} else {
			if (driver.findElement(By.xpath("//div[@class='hardware-status']"))
					.getDomProperty("innerText").equalsIgnoreCase("(STOPPED)")) {
				driver.findElement(By.xpath("//button[@class='btn btn-menu']")).click();
				driver.findElement(By.xpath("//p[@title='Logout']")).click();
				Thread.sleep(1000);
				driver.findElement(By.xpath("//div[@class='dialog-modal-content size-auto position-center']" +
						"//button[@class='btn btn btn-confirm ml-3']")).click();
			} else {
				driver.findElement(By.xpath("//button[@class='btn btn-menu']")).click();
				driver.findElement(By.xpath("//p[@title='Stop Bin Induction']")).click();
				Thread.sleep(1000);
				driver.findElement(By.xpath("//button[@class='btn btn-menu']")).click();
				driver.findElement(By.xpath("//p[@title='Logout']")).click();
				Thread.sleep(1000);
				driver.findElement(By.xpath("//div[@class='dialog-modal-content size-auto position-center']" +
						"//button[@class='btn btn btn-confirm ml-3']")).click();
			}
		}
		driver.close();
		driver.quit();
	}
    
}