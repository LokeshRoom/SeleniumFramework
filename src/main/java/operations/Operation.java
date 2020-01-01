package operations;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import objectutils.ObjectReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterMethod;
import testngrunner.StartTest;

import java.io.File;


public class Operation extends ObjectReader {

    private WebDriver driver;
    private JSONObject yamlJsonObject;
    private ExtentReports extentReports;

    private Logger logger = LogManager.getLogger("Operation");
    private ExtentTest extentTest;
    private ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports\\Reports.html");
    public Operation() throws Exception {
        switch (StartTest.getBrowser()) {
            case CHROME:
                driver = new ChromeDriver();
                break;
            case IE:
                driver = new InternetExplorerDriver();
                break;
            case EDGE:
                driver = new EdgeDriver();
                break;
            case FIREFOX:
                driver = new FirefoxDriver();
                break;
        }

        driver.manage().window().maximize();
        extentReports = new ExtentReports();
        extentReports.attachReporter(extentHtmlReporter);
        extentTest = extentReports.createTest("My Test Case Name");
        this.setYamlJsonObject(StartTest.getYamlFile(), System.getProperty("user.dir") + "\\src\\test\\ObjectRepository\\");
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
        context.setConfigLocation(file.toURI());
    }


    public JSONObject getYamlJsonObject() {
        return yamlJsonObject;
    }

    public void setYamlJsonObject(String fileName, String path) throws Exception {
        this.yamlJsonObject = ObjectReader.readYaml(fileName, path);
    }

    private WebElement findElement(String element, String page) throws Exception {
        return driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page));
    }

    public void goToUrl(String urlObject) {
        String url = ObjectReader.getUrl(yamlJsonObject, urlObject);
        driver.get(url);
        extentTest.log(Status.PASS, "Launched Url: " + url);
        logger.error("Launched Url:" + url);

    }

    public void navigateToUrl(String url) {
        driver.navigate().to(url);
        extentTest.log(Status.PASS, "Navigate to URL: " + url);
    }

    public void enterText(String element, String page, CharSequence text) throws Exception {
        WebElement webElement = this.findElement(element, page);
        webElement.sendKeys(text);
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.PASS, "Enterted Text: " + text);
        extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info("Enterted Text: " + text + " in  object: " + element + " page: " + page);
    }

    public void click(String element, String page) throws Exception {
        WebElement webElement = this.findElement(element, page);
        webElement.click();
        extentTest.log(Status.PASS, "Clicked on element: " + element);
        extentReports.flush();
    }

    public void doubleClick(String element, String page) {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.doubleClick(webElement).perform();
            extentTest.log(Status.PASS, "Double clicked on Element" + element);
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Double click failed on Element:" + e);
        }
    }

    public void rightClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.contextClick(webElement).build().perform();
            extentTest.log(Status.PASS, "Right click on Element" + element);
        } catch (Exception e) {
            extentTest.log(Status.FAIL, "Right click failed on Element:" + e);
            throw e;
        }
    }

    @AfterMethod
    public void exit() {
        driver.quit();
        extentReports.flush();

    }

}
