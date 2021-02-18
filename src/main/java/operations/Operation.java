package operations;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import objectutils.ObjectReader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import testngrunner.StartTest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class Operation extends ObjectReader {

    private WebDriver driver;
    private JSONObject yamlJsonObject;

    private static Logger logger = LogManager.getLogger("Operation");
    ExtentReports reports = ExtentReporterUtils.getExtentReports();
    ExtentTest extentTest;

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
            case REMOTE_CHROME:
                DesiredCapabilities chromeOptions= new DesiredCapabilities();
                chromeOptions.setBrowserName(BrowserType.CHROME);
                chromeOptions.setPlatform(Platform.LINUX);
                chromeOptions.setAcceptInsecureCerts(true);
                chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
                chromeOptions.acceptInsecureCerts();
                driver=new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),chromeOptions);
        }

        extentTest = reports.createTest(Thread.currentThread()
                .getStackTrace()[3]
                .getMethodName());
        driver.manage().window().maximize();
        this.setYamlJsonObject(StartTest.getYamlFile(), System.getProperty("user.dir") + "\\src\\test\\ObjectRepository\\");
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
        context.setConfigLocation(file.toURI());
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }

    void pass(String message) throws IOException {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.PASS, message);
        extentTest.log(Status.PASS, " test", MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
    }

    void fail(String message, Exception e) throws Exception {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
        extentTest.log(Status.FAIL, message + "\n" + e);
        extentTest.log(Status.FAIL, "TEst ", MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        //extentTest.addScreenCaptureFromBase64String(scrst);
        logger.info(message);
        throw new Exception(e);
    }

    String getScreenshot() throws IOException {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File scrst = screenshot.getScreenshotAs(OutputType.FILE);
        File dest = new File(System.getProperty("user.dir") + "\\target\\screenshots\\scrst.png");
        FileUtils.copyFile(scrst, dest);
        return System.getProperty("user.dir") + "\\target\\screenshots\\scrst.png";
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

    public void goToUrl(String urlObject) throws Exception {
        String url = ObjectReader.getUrl(yamlJsonObject, urlObject);
        try {
            driver.get(url);
            pass("Launched Url: " + url);
        } catch (Exception e) {
            fail("Failed to launch url: " + url + "/n", e);
        }
    }

    public void navigateToUrl(String url) throws Exception {
        try {
            driver.navigate().to(url);
            pass("Navigate to URL: " + url);
        } catch (Exception e) {
            fail("Failed to navigate to Url" + url + "/n", e);
        }
    }

    public void enterText(String element, String page, CharSequence text) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            webElement.sendKeys(text);
            pass("Enterted Text: " + text + " in  object: " + element + " page: " + page);
        } catch (Exception e) {
            fail("Enterted Text: " + text + " in  object: " + element + " of page: " + page, e);
        }
    }

    public void click(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            webElement.click();
            pass("Clicked on element: " + element + "on page: " + page);
        } catch (Exception e) {
            fail("Clicked on element: " + element + "in page: " + page, e);
        }
    }

    public void doubleClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.doubleClick(webElement).perform();
            pass("Double clicked on Element: " + element + "in page: " + page);
        } catch (Exception e) {
            fail("Failed to double click failed on Element:" + e, e);
        }
    }

    public void rightClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.contextClick(webElement).build().perform();
            pass("Right clicked on Element" + element + "in page: " + page);
        } catch (Exception e) {
            fail("Right click failed on Element:" + element, e);
        }
    }

    public void selectFromDropDownByText(String element, String page, String option) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            select.selectByVisibleText(option);
            pass("Selected " + option + " from dropdown: " + element + " on page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + option + " from dropdown: " + element + " on page:" + page, e);
        }
    }

    public void selectFromDropdownByIndex(String element, String page, int index) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            select.selectByIndex(index);
            pass("Selected " + index + " from dropdown: " + element + " on page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + index + " from dropdown: " + element + " on page:" + page, e);
        }
    }

    public void selectFromDropdownByValue(String element, String page, String value) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            select.selectByValue(value);
            pass("Selected " + value + " from dropdown: " + element + " on page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + value + " from dropdown: " + element + " on page:" + page, e);

        }
    }

    public void javaScriptExecutorClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            JavascriptExecutor je = (JavascriptExecutor) driver;
            je.executeScript("arguments[].click()", webElement);
            pass("JavaScriptExecutor clicked on Element: " + element + " in page:" + page);
        } catch (Exception e) {
            fail("JavaScriptExecutor failed to click on Element: " + element + " in page:" + page, e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void quit() {
        driver.quit();
        reports.flush();
    }

    @BeforeMethod
    public void extentTestSetup() {

    }

    @BeforeSuite(alwaysRun = true)
    public void extentSetup() {
        String date = ((new SimpleDateFormat("ddMMMyyHHmmss")).format(new Date()));
        Map<String, String> map = (Map) System.getProperties();
        ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports\\Automation Summary Report " + date + ".html");
        ExtentReporterUtils.setExtentHtmlReporter(extentHtmlReporter);
        ExtentReports extentReports = new ExtentReports();
        ExtentReporterUtils.setExtentReports(extentReports);
        extentReports.attachReporter(extentHtmlReporter);
        extentHtmlReporter.config().setTheme(Theme.DARK);
        extentHtmlReporter.config().setDocumentTitle("Automation Reports");
        extentHtmlReporter.config().setReportName("Selenium Framework Test Reports");
        extentReports.setSystemInfo("Os", map.get("os.name"));
        extentReports.setSystemInfo("User", map.get("user.name"));
        extentReports.setSystemInfo("Java Version", map.get("java.version"));
        extentReports.setSystemInfo("Environment", StartTest.getEnv());
        System.out.println("------------------------------Tests Started------------------------------");
    }


}
