import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeleniumXeroTest {

    protected static WebDriver driver;

    @BeforeClass
    public static void setup() {
        //FirefoxDriver driver=new FirefoxDriver();
        //ChromeDriver driver = new ChromeDriver();
        System.setProperty("webdriver.chrome.driver", "../ChromeDriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Before
    public void loginToXero() throws Exception {
        driver.get("https://login.xero.com/identity/user/login");
        driver.findElement(By.xpath("//input[@data-automationid='Username--input']")).sendKeys("agmanzo@gmail.com");
        String pw = new String(Base64.getDecoder().decode("Q29uYW4yMDE5".getBytes()));
        driver.findElement(By.xpath("//input[@data-automationid='PassWord--input']")).sendKeys(pw);
        driver.findElement(By.xpath("//button[@data-automationid='LoginSubmit--button']")).click();

        if (driver.getPageSource().contains("Enter the 6-digit code found in your authenticator app")) {
            driver.findElement(By.xpath("//button[@data-automationid='auth-continuebutton']")).click();
            driver.findElement(By.xpath("//button[@data-automationid='auth-authwithsecurityquestionsbutton']")).click();

            String firstSecQ = "What is your dream car?";
            String secondSecQ = "What is your dream job?";
            String thirdSecQ = "What was the name of your first pet?";

            Map secQPair = new HashMap<String, String>();
            secQPair.put(firstSecQ, new String(Base64.getDecoder().decode("VGVzdGluZzE=".getBytes())));
            secQPair.put(secondSecQ, new String(Base64.getDecoder().decode("VGVzdGluZzI=".getBytes())));
            secQPair.put(thirdSecQ, new String(Base64.getDecoder().decode("VGVzdGluZzM=".getBytes())));

            String firstActualSecQ = driver.findElement(By.xpath("//label[@data-automationid='auth-firstanswer--label']")).getText();
            String secondActualSecQ = driver.findElement(By.xpath("//label[@data-automationid='auth-secondanswer--label']")).getText();

            //answer security questions
            driver.findElement(By.xpath("//input[@data-automationid='auth-firstanswer--input']")).sendKeys(secQPair.get(firstActualSecQ).toString());
            driver.findElement(By.xpath("//input[@data-automationid='auth-secondanswer--input']")).sendKeys(secQPair.get(secondActualSecQ).toString());
            driver.findElement(By.xpath("//button[@data-automationid='auth-submitanswersbutton']")).click();
        }
    }


    @Test
    public void testCreateANZAccount() {

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//button[@data-name='navigation-menu/accounting']")).click();
        driver.findElement(By.xpath("//a[@data-name='navigation-menu/accounting/bank-accounts']")).click();

        driver.findElement(By.xpath("//span[@data-automationid='Add Bank Account-button']")).click();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//*[text()='ANZ (NZ)']")).click();

        String accountName = new Date().toString();
        driver.findElement(By.xpath("//input[@id='accountname-1037-inputEl']")).sendKeys(accountName);
        //select loans
        WebElement testDropDown = driver.findElement(By.xpath("//input[@id='accounttype-1039-inputEl']"));
        testDropDown.sendKeys(Keys.DOWN);
        testDropDown.sendKeys(Keys.DOWN);
        testDropDown.sendKeys(Keys.RETURN);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//input[@id='accountnumber-1068-inputEl']")).sendKeys("1234567890");

        driver.findElement(By.xpath("//*[text()='Continue']")).click();

        //driver.getPageSource().contains();
        WebDriverWait wait = new WebDriverWait(driver,30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@data-automationid='connectbank-buttonDownloadForm']")));

        //do verification
        verifyANZAccount(accountName);

    }

    @After
    public void teardown() {
        driver.close();
    }

    void verifyANZAccount(String accountName) {
        //driver.findElement(By.xpath("//button[@data-automationid='connectbank-buttonDownloadForm']"));
        Boolean actualANZLogoDisplayCheck = driver.findElement(By.xpath("//img[@alt='ANZ (NZ)']")).isDisplayed();
        Assert.assertTrue("ANZ logo displayed after creation of account", actualANZLogoDisplayCheck);

        //check if account was created with the accountname defined
        Boolean actualAcctNameCheck = driver.getPageSource().contains(accountName);
        Assert.assertTrue("Account created with the accountName="+accountName, actualAcctNameCheck);
    }
}