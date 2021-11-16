package operations;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import driverfactory.Browsers;
import mailutils.PasswordDecoder;
import objectutils.ObjectReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestListener;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import testngrunner.StartTest;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.*;


public class Operation extends ObjectReader implements ITestListener {

    private static Logger logger = LogManager.getLogger("Operation");
    public boolean highlightElement = false;
    public boolean setMoveToElement = true;

    private boolean quitDriverOnEndOfTest = false;

    public boolean isQuitDriverOnEndOfTest() {
        return quitDriverOnEndOfTest;
    }

    public void setQuitDriverOnEndOfTest(boolean quitDriverOnEndOfTest) {
        if (System.getProperty("closebrowser") == null)
            this.quitDriverOnEndOfTest = quitDriverOnEndOfTest;
        else
            this.quitDriverOnEndOfTest = Boolean.parseBoolean(System.getProperty("closebrowser"));
    }

    boolean frameSetMoveToElement = false;
    ExtentHtmlReporter extentHtmlReporter = ExtentReporterUtils.getExtentHtmlReporter();
    ExtentReports extentReports = ExtentReporterUtils.getExtentReports();
    ExtentTest extentTest;
    List<WebDriver> drivers = new ArrayList<>();
    private WebDriver driver;
    private JSONObject yamlJsonObject;
    private Boolean setScreenShotForEachStep = false;
    private int waitTimeout = 10;
    private String port = " ";
    public Operation() {

    }

    /**
     * @param browser
     * @throws Exception
     * @author Lokesh.kk
     */
    public Operation(Browsers browser, String... testDescriptionForReports) throws Exception {
        try {
            switch (browser) {
                case CHROME:
                    System.setProperty("webdriver.chrome.silentOutput", "true");
                    ChromeOptions options1 = new ChromeOptions();
                    options1.addArguments("--silent", "--disable-notifications");
                    options1.addArguments("--start-maximized");
                    options1.addArguments("disable-popup-blocking");
                    driver = new ChromeDriver(options1);
                    Map<String, String> cap = (Map) ((ChromeDriver) driver).getCapabilities().asMap().get("goog:chromeOptions");
                    port = cap.get("debuggerAddress");
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
                case CHROME_HEADLESS:
                    System.setProperty("webdriver.chrome.silentOutput", "true");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless");
                    options.addArguments("--window-size=1536,774");
                    options.addArguments("--silent", "--disable-notifications");
                    options.addArguments("disable-popup-blocking");
                    driver = new ChromeDriver(options);

            }
            driver.manage().deleteAllCookies();
            if (extentReports == null)
                extentSetup();
            if (browser == Browsers.EDGE)
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
            else
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            driver.manage().deleteAllCookies();
            if (testDescriptionForReports.length > 0)
                extentTest = extentReports.createTest(testDescriptionForReports[0]);
            else
                extentTest = extentReports.createTest(Thread.currentThread().getStackTrace()[3].getMethodName());
            if (!port.equals(" "))
                System.out.println("Chrome Port: " + port);
            driver.manage().window().maximize();
            drivers.add(driver);
            this.setYamlJsonObject(StartTest.getYamlFile(), System.getProperty("user.dir") + "\\src\\test\\ObjectRepository\\");
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
            context.setConfigLocation(file.toURI());
            try {
                if (System.getProperty("screenshots").equalsIgnoreCase("true")) {
                    setScreenShotForEachStep = true;
                }
            } catch (Exception e) {
                setScreenShotForEachStep = StartTest.setScreenshotforEachStep;
            }
            setQuitDriverOnEndOfTest(false);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }

    }

    public Operation(String port, String... testDescriptionForReports) throws Exception {
        ChromeOptions options1 = new ChromeOptions();
        options1.setExperimentalOption("debuggerAddress", port);
        driver = new ChromeDriver(options1);
        driver.manage().deleteAllCookies();
        if (extentReports == null)
            extentSetup();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().deleteAllCookies();
        if (testDescriptionForReports.length > 0)
            extentTest = extentReports.createTest(testDescriptionForReports[0]);
        else
            extentTest = extentReports.createTest(Thread.currentThread().getStackTrace()[3].getMethodName());
        driver.manage().window().maximize();
        drivers.add(driver);
        this.setYamlJsonObject(StartTest.getYamlFile(), System.getProperty("user.dir") + "\\src\\test\\ObjectRepository\\");
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
        context.setConfigLocation(file.toURI());
        try {
            if (System.getProperty("screenshots").equalsIgnoreCase("true")) {
                setScreenShotForEachStep = true;
            }
        } catch (Exception e) {
            setScreenShotForEachStep = StartTest.setScreenshotforEachStep;
        }
    }


    public Operation(Browsers browser, Operation operation) throws Exception {
        try {
            switch (browser) {
                case CHROME:
                    System.setProperty("webdriver.chrome.silentOutput", "true");
                    ChromeOptions options1 = new ChromeOptions();
                    options1.addArguments("--silent", "--disable-notifications");
                    options1.addArguments("disable-popup-blocking");
                    driver = new ChromeDriver(options1);
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
                case CHROME_HEADLESS:
                    System.setProperty("webdriver.chrome.silentOutput", "true");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless", "start-maximized");
                    options.addArguments("--window-size=1536,774");
                    options.addArguments("--silent", "--disable-notifications");
                    options.addArguments("disable-popup-blocking");
                    driver = new ChromeDriver(options);
            }
            if (extentReports == null)
                extentSetup();
            if (browser == Browsers.EDGE)
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
            else
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            driver.manage().deleteAllCookies();
            driver.manage().window().maximize();
            this.extentTest = operation.extentTest;
            this.drivers = operation.drivers;
            drivers.add(driver);
            this.setYamlJsonObject(StartTest.getYamlFile(), System.getProperty("user.dir") + "\\src\\test\\ObjectRepository\\");
            LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
            File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
            context.setConfigLocation(file.toURI());
            setQuitDriverOnEndOfTest(false);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }


    public Boolean getSetScreenShotForEachStep() {
        return setScreenShotForEachStep;
    }

    public void setSetScreenShotForEachStep(Boolean setScreenshotforEachStep) {
        this.setScreenShotForEachStep = setScreenshotforEachStep;
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }

    void pass(String message) throws IOException {
        if (setScreenShotForEachStep) {
            String scrst = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            extentTest.pass(message, MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());

        } else {
            extentTest.pass(message);
        }

        logger.info(message);
    }

    /**
     * @param message
     * @param e
     * @throws Exception
     * @author Lokesh.kk
     */
    void fail(String message, Exception e) throws Exception {
        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(screenshot.getImage(), "png", os);
        String scrst = Base64.getEncoder().encodeToString(os.toByteArray());
        // String scrst = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        String currentUrl = driver.getCurrentUrl();
        extentTest.fail(message + "\n" + e + "\n Page Url: " + currentUrl + "\n", MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        logger.info(message + "/n" + currentUrl + "\n" + e);
        exit();
        throw new Exception(e);
    }

    /**
     * @param message
     * @throws Exception
     * @author Lokesh.kk
     */
    void log(String message) throws Exception {
        if (StartTest.setScreenshotforEachStep) {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            String scrst = screenshot.getScreenshotAs(OutputType.BASE64);
            extentTest.log(Status.INFO, message, MediaEntityBuilder.createScreenCaptureFromBase64String(scrst).build());
        } else {
            extentTest.log(Status.INFO, message);
        }
    }

    /**
     * @return
     * @author Lokesh.kk
     */
    public JSONObject getYamlJsonObject() {
        return yamlJsonObject;
    }

    /**
     * @param fileName
     * @param path
     * @throws Exception
     * @author Lokesh.kk
     */
    public void setYamlJsonObject(String fileName, String path) throws Exception {
        this.yamlJsonObject = ObjectReader.readYaml(fileName, path);
    }

    /**
     * @author Lokesh.kk
     */
    private void waitUntilpageLoaded() throws InterruptedException {
        JavaScriptWaits javaScriptWaits = new JavaScriptWaits(driver);
        javaScriptWaits.waitAllRequest();
    }

    /**
     * @param webelement
     * @author Lokesh.kk
     */
    public void waitUntilElementGetDisplayed(WebElement webelement) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver ->
                ExpectedConditions.visibilityOf(webelement));
    }

    public void waitUntilTextDisplayed(String text) throws Exception {
        try {
            waitUntilpageLoaded();
            int i = 0;
            while (!driver.findElement(By.xpath("//*[text()='" + text + "']")).isDisplayed()) {
                wait(1);
                i++;
                if (i > 10)
                    break;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /**
     * @param element
     * @author Lokesh.kk
     */
    public void waitUntilElementLoaded(String element, String page, String... appendValueToLocator) throws Exception {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver ->
        {
            try {
                return ExpectedConditions.visibilityOf(driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator)));
            } catch (Exception e) {
                try {
                    fail(e.getMessage(), e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        });
    }

    /**
     * @param element
     * @author Lokesh.kk
     */
    public void waitUntilElementLoaded(WebElement element, int seconds) throws Exception {
        new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(driver ->
        {
            try {
                return ExpectedConditions.visibilityOf(element);
            } catch (Exception e) {
                try {
                    fail(e.getMessage(), e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            return null;
        });
    }

    /**
     * @param element
     * @param page
     * @return webelement
     * @throws Exception
     * @author Lokesh Kumar
     * Usage: returns the webelement
     */
    private WebElement findElement(String element, String page, String... appendValueToLocator) throws Exception {
        waitUntilpageLoaded();
        WebElement element1 = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator));
        if (setMoveToElement) {
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(element1).build().perform();
            } catch (Exception e) {

            }
            // ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element1);
        }
        if (highlightElement) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);",
                    element1, " border: 2px solid red;");
        }
        return element1;
    }

    private WebElement findElementFrame(String element, String page, String... appendValueToLocator) throws Exception {
        waitUntilpageLoaded();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        WebElement element1 = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        return element1;
    }

    public WebElement findElement(By by) throws Exception {
        WebElement element1 = null;
        try {
            waitUntilpageLoaded();
            element1 = driver.findElement(by);
            /*Actions actions = new Actions(driver);
            actions.moveToElement(element1).build().perform();*/
            return element1;
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return element1;
    }

    /**
     * @param element
     * @param page
     * @return size of given element
     * @throws Exception
     * @author: Lokesh.kk
     */
    public int getWebElementsSize(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            return driver.findElements(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator)).size();
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * @param element
     * @param page
     * @return list of webelements
     * @throws Exception
     * @author: Lokesh.kk
     */
    public List<WebElement> getWebElements(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            return driver.findElements(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator));
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Creator: Lokesh Kumar
     * Created on: Feb 3, 2020
     * Usage: To get title of the web page
     */
    public String getTilte() {
        return driver.getTitle();
    }

    /**
     * Creator: Lokesh Kumar
     * Created on: Feb 3, 2020
     * Usage: Return the attribute of the element
     *
     * @param element
     * @param page
     * @param attribute
     * @throws Exception
     */
    public String getAttributeValue(String element, String page, String attribute, String... appendValueToXpath) throws Exception {
        String attributeValue = "";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            attributeValue = webElement.getAttribute(attribute);
            log("Attribute Value of " + element + " is [" + attributeValue + "]");
        } catch (Exception e) {
            fail("Failed to get attribute value of element: " + element + " in " + page, e);
        }
        return attributeValue;
    }

    /**
     * @param urlObject
     * @throws Exception
     * @author: Lokesh Kumar
     * Drivers gets the given url
     */
    public void goToUrl(String urlObject, String... appendToUrl) throws Exception {
        String url = ObjectReader.getUrl(yamlJsonObject, urlObject);
        if (appendToUrl.length > 0)
            url = url + appendToUrl[0];

        try {
            driver.get(url);
            pass("Launched Url: " + url);
        } catch (Exception e) {
            fail("Failed to launch url: " + url + "/n", e);
        }
    }

    /**
     * @param url
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Navigate to given url
     */
    public void navigateToUrl(String url) throws Exception {
        try {
            driver.navigate().to(url);
            pass("Navigate to URL: " + url);
        } catch (Exception e) {
            fail("Failed to navigate to Url" + url + "/n", e);
        }
    }

    /**
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: To delete all cookies
     */
    public void deleteAllCookies() throws Exception {
        try {
            driver.manage().deleteAllCookies();
            pass("Deleted All Cookies");
        } catch (Exception e) {
            fail("Failed to delete cookies \n", e);
        }
    }

    /**
     * @param element
     * @param page
     * @param text
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Enters text in the provided weblement
     */
    public void enterText(String element, String page, CharSequence text, String... appendValueToXpath) throws Exception {
        try {
            // new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(this.findElement(element, page, appendValueToXpath)));
            waitUntilElementVisible(element, page, waitTimeout, appendValueToXpath);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            webElement.sendKeys(text);
            pass("Entered Text: " + text + " in  object: " + element + " page: " + page);
        } catch (Exception e) {
            fail("Failed to enter Text: " + text + " in  object: " + element + " of page: " + page, e);
        }
    }

    public void enterPassword(String element, String page, String text, String... appendValueToXpath) throws Exception {
        try {
            // new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(this.findElement(element, page, appendValueToXpath)));
            waitUntilElementVisible(element, page, waitTimeout, appendValueToXpath);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            webElement.sendKeys(PasswordDecoder.decoder(text));
            pass("Enterted password in  object: " + element + " page: " + page);
        } catch (Exception e) {
            fail("Failed to enter password in  object: " + element + " of page: " + page, e);
        }
    }

    /**
     * @param element
     * @param page
     * @param filePath
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: for upoloading file in path
     */
    public void uploadFile(String element, String page, CharSequence filePath, String... appendValueToXpath) throws Exception {
        try {
            // new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(this.findElement(element, page, appendValueToXpath)));
            boolean scens = StartTest.setScreenshotforEachStep;
            boolean hov = setMoveToElement;
            StartTest.setScreenshotforEachStep = false;
            setMoveToElement = false;
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            webElement.sendKeys(filePath);
            pass("Enterted Text: " + filePath + " in  object: " + element + " page: " + page);
            StartTest.setScreenshotforEachStep = scens;
            setMoveToElement = hov;
        } catch (Exception e) {
            fail("Failed to enter Text: " + filePath + " in  object: " + element + " of page: " + page, e);
        }
    }

    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Clears text in the provided weblement
     */
    public void clearText(String element, String page, String... appendValueToXpath) throws Exception {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until(driver -> {
                try {
                    return ExpectedConditions.elementToBeClickable(this.findElement(element, page));
                } catch (Exception e) {

                }
                return null;
            });
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            webElement.clear();
            pass("Cleared Text in  object: " + element + " page: " + page);
        } catch (Exception e) {
            fail("Failed to clear text in  object: " + element + " of page: " + page, e);
        }
    }


    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Performs click on element
     */
    public void click(String element, String page, String... appendValueToXpath) throws Exception {

        try {
            waitUntilElementVisible(element, page, waitTimeout, appendValueToXpath);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            webElement.click();
            pass("Clicked on element: " + element + " in page: " + page);
        } catch (Exception e) {
            fail("Failed to click on element : " + element + " in page: " + page, e);
        }
    }

    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: submit forms
     */
    public void submit(String element, String page, String... appendValueToXpath) throws Exception {

        try {
            waitUntilElementVisible(element, page, waitTimeout, appendValueToXpath);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            webElement.submit();
            pass("Submitted form: " + element + " in page " + page);
        } catch (Exception e) {
            fail("Failed to submit form: " + element + " in page: " + page, e);
        }
    }

    /**
     * @param element
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Performs click on element
     */
    public void click(WebElement element) throws Exception {
        try {

            element.click();
            pass("Clicked on element: " + element);
        } catch (Exception e) {
            fail("Failed to click on element: " + element, e);
        }
    }

    public void waitUntilElementVisible(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            int i = 0;
            //boolean flag = true;
            while (!this.isDisplayed(element, page, appendValueToLocator)) {
                i++;
                wait(1);
                if (i > 5) {
                    //flag = false;
                    break;
                    //throw new NoSuchElementException(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator).toString());
                }
            }
        } catch (Exception e) {
            fail("Element is not displaying : " + element + " in page: " + page, e);
        }
    }

    public void waitUntilElementVisible(String element, String page, int timeoutSeconds, String... appendValueToLocator) throws Exception {
        try {
            int i = 0;
            //boolean flag = true;
            while (!this.isDisplayed(element, page, appendValueToLocator)) {
                i++;
                wait(1);
                if (i > timeoutSeconds) {
                    //flag = false;
                    break;
                    //throw new NoSuchElementException(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator).toString());
                }
            }
        } catch (Exception e) {
            fail("Element is not displaying : " + element + " in page: " + page, e);
        }
    }

    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Performs the double click on the element
     */
    public void doubleClick(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            Actions actions = new Actions(driver);
            actions.doubleClick(webElement).perform();
            pass("Double clicked on Element: " + element + " in page: " + page);
        } catch (Exception e) {
            fail("Failed to double on Element:" + e, e);
        }
    }

    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Performs rightclick on the element
     */
    public void rightClick(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
            Actions actions = new Actions(driver);
            actions.contextClick(webElement).build().perform();
            pass("Right clicked on Element" + element + " in page: " + page);
        } catch (Exception e) {
            fail("Right click failed on Element:" + element, e);
        }
    }

    /**
     * @param element
     * @param page
     * @param option
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Select element from dropdown using the text of the element
     */
    public void selectFromDropDownByText(String element, String page, String option, String... appendValueToXpath) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            Select select = new Select(webElement);
            select.selectByVisibleText(option);
            pass("Selected " + option + " from dropdown: " + element + " in page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + option + " from dropdown: " + element + " in page:" + page, e);
        }
    }

    public List<WebElement> getAllOptionsFromDropdown(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            Select select = new Select(webElement);
            log("Returned dropdownoptions of: " + element + " in page:" + page);
            return select.getOptions();
        } catch (Exception e) {
            fail("Failed to return options from dropdown: " + element + " in page:" + page, e);
        }
        return null;
    }

    /**
     * @param element
     * @param page
     * @param index
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Select element from dropdown using the index of the element
     */
    public void selectFromDropdownByIndex(String element, String page, int index, String... appendValueToXpath) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            Select select = new Select(webElement);
            select.selectByIndex(index);
            pass("Selected indexed " + index + " option from dropdown: " + element + " in page:" + page);
        } catch (Exception e) {
            fail("Failed to select indexed" + index + " option from dropdown: " + element + " in page:" + page, e);
        }
    }

    /**
     * @param element
     * @param page
     * @param value
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Select from dropdown using the value of element
     */
    public void selectFromDropdownByValue(String element, String page, String value, String... appendValueToXpath) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            Select select = new Select(webElement);
            select.selectByValue(value);
            pass("Selected " + value + " from dropdown: " + element + " in page:" + page);
        } catch (Exception e) {
            fail("Failed to select " + value + " from dropdown: " + element + " in page:" + page, e);

        }
    }

    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: performs Javascriptexecutor click on element
     */
    public void javaScriptExecutorClick(String element, String page, String... appendValueToXpath) throws Exception {
        try {
            waitUntilElementVisible(element, page, waitTimeout, appendValueToXpath);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            JavascriptExecutor je = (JavascriptExecutor) driver;
            je.executeScript("arguments[0].click()", webElement);
            pass("JavaScriptExecutor clicked on Element: " + element + " in page:" + page);
        } catch (Exception e) {
            fail("JavaScriptExecutor failed to click on Element: " + element + " in page:" + page, e);
        }
    }

    /**
     * @param element
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: performs Javascriptexecutor click on element
     */
    public void javaScriptExecutorClick(WebElement element) throws Exception {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver -> ExpectedConditions.elementToBeClickable(element));
            JavascriptExecutor je = (JavascriptExecutor) driver;
            je.executeScript("arguments[0].click()", element);
            pass("JavaScriptExecutor clicked on Element: " + element);
        } catch (Exception e) {
            fail("JavaScriptExecutor failed to click on Element: " + element, e);
        }
    }

    /**
     * @param windowName
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Switches to window using name
     */
    public void switchWindow(String windowName) throws Exception {
        try {
            driver.switchTo().window(windowName);
            pass("Switched to Window: " + windowName);
        } catch (Exception e) {
            fail("Failed to Switch Window: " + windowName + " : ", e);
        }
    }

    /**
     * @param windowIndex
     * @throws Exception
     * @author: Lokesh Kumar
     * Usage: Switches the windows using Index
     */
    public void switchWindow(int windowIndex) throws Exception {
        int window = 1;
        try {
            Set<String> windowHandles = driver.getWindowHandles();
            List<String> windowStrings = new ArrayList<>(windowHandles);
            window = windowStrings.size();
            String reqWindow = windowStrings.get(windowIndex);
            driver.switchTo().window(reqWindow);
            driver.manage().window().maximize();
            pass("Switched to Window having Index: " + reqWindow);
        } catch (Exception e) {
            fail("Failed to Switch Window having Index: " + windowIndex + " Total Windows: " + window, e);
        }
    }

    /**
     * @param windowName
     * @throws Exception
     * @author Lokesh.kk
     */
    public void closeWindow(String... windowName) throws Exception {
        try {
            if (windowName.length > 0)
                this.switchWindow(windowName[0]);
            driver.close();
        } catch (Exception e) {
            fail("Failed to close window", e);
        }
    }

    /**
     * @param windowIndex
     * @throws Exception
     * @author Lokesh.kk
     */
    public void closeWindow(int windowIndex) throws Exception {
        try {
            this.switchWindow(windowIndex);
            driver.close();
        } catch (Exception e) {
            fail("Failed to close window: " + windowIndex, e);
        }
    }

    /**
     * @param element
     * @param page
     * @param expectedText
     * @throws Exception
     * @author: Lokesh Kumar
     * usage: Asserts the text of the element
     */
    public void assertElementText(String element, String page, String expectedText, String... appendValueToLocator) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");

            if (actualText.equals(expectedText) && webElement.isDisplayed())
                pass("Actual Text [" + actualText + "] of object " + element + " is matching with expected text [" + expectedText + "]");
            else throw new Exception("Actual Text is not matching with expected text or Element is not displaying");
        } catch (Exception e) {
            fail("Actual Text [" + actualText + "] of object " + element + " is not matching with expected text [" + expectedText + "]", e);
        }
    }

    public void assertElementText(String element, String page, String expectedText, Boolean trueOrFalse, String... appendValueToLocator) throws Exception {
        String actualText = "";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");
            if (trueOrFalse) {
                if (actualText.equals(expectedText) && webElement.isDisplayed())
                    pass("Actual Text [" + actualText + "] of object " + element + " is matching with expected text [" + expectedText + "]");
                else throw new Exception("Actual Text is not matching with expected text or Element is not displaying");
            } else {
                if (!actualText.equals(expectedText))
                    pass("Actual Text [" + actualText + "] of object " + element + " is not matching with expected text [" + expectedText + "]");
                else
                    throw new Exception("Actual Text [" + actualText + "]is matching with [" + expectedText + "] text");
            }

        } catch (Exception e) {
            if (trueOrFalse)
                fail("Actual Text [" + actualText + "] of object " + element + " is not matching with expected text [" + expectedText + "]", e);
            else
                fail("Actual Text [" + actualText + "] of object " + element + " is matching with expected text [" + expectedText + "]", e);
        }
    }

    public void assertElementDisplayed(String element, String page, boolean trueOrFalse, String... appendValueToLocator) throws Exception {
        try {
            if (trueOrFalse) {
                if (this.isDisplayed(element, page, appendValueToLocator))
                    pass("Element " + element + " is Displaying " + "in " + page);
                else
                    throw new NoSuchElementException("Element is not displaying");
            } else {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
                if (this.getWebElements(element, page, appendValueToLocator).size() == 0)
                    pass("Element " + element + " is not Displaying: " + "in " + page);
                else if (!this.isDisplayed(element, page, appendValueToLocator))
                    pass("Element " + element + " is not Displaying: " + "in " + page);
                else
                    throw new Exception("Element is displaying which is not expected");
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            }
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            if (trueOrFalse)
                fail("Element " + element + " is not Displaying: " + "in " + page, e);
            else
                fail("Element " + element + " is Displaying: " + "in " + page, e);
        }
    }

    public String getCssValue(String element, String page, String value, String... appendValueToXpath) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getCssValue(value);

            log("Css value  of an element " + element + ": " + actualText);
        } catch (StaleElementReferenceException e) {
            wait(4);
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getCssValue(value);
        } catch (Exception e) {
            fail("Get css value failed for an element " + element, e);
        }
        return actualText;

    }

    /**
     * @param element
     * @param page
     * @param expectedValue
     * @throws Exception
     * @author: Lokesh Kumar
     * usage: Asserts the attribute of the element
     */
    public void assertAttributeValue(String element, String page, String attribute, String expectedValue, String... appendValueToXpath) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualValue = webElement.getAttribute(attribute);
            if (actualValue.equals(expectedValue) && webElement.isDisplayed())
                pass("Actual value: [" + actualValue + "] of " + attribute + " is matching expected [" + expectedValue + "]");
            else
                throw new Exception("Actual value is not matching with expected value or element is not displaying");
        } catch (Exception e) {
            fail("Actual Value: [" + actualValue + "] is not matching with expected [" + expectedValue + "] of " + element + "'s attribute" + attribute, e);
        }
    }

    /**
     * @param element
     * @param page
     * @param checkedOrNot
     * @throws Exception
     * @author: Lokesh Kumar
     * usage: Asserts the attribute of the element
     */
    public void assertCheckbox(String element, String page, boolean checkedOrNot, String... appendValueToXpath) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);

            if (checkedOrNot) {
                if (webElement.isSelected())
                    pass("Check box :" + element + "] in " + page + " is checked");
                else
                    fail("Check box :" + element + "] in " + page + " is not checked", new Exception("Checkbox is not checked"));
            } else {
                if (!webElement.isSelected())
                    pass("Check box :" + element + "] in " + page + " is not checked");
                else
                    fail("Check box :" + element + "] in " + page + " is checked which is not expected", new Exception("Checkbox is checked"));

            }

        } catch (Exception e) {
            fail("Checkbox assertion failed", e);
        }
    }

    /**
     * @param element
     * @param page
     * @param expectedValue
     * @throws Exception
     * @author: Lokesh Kumar
     * usage: Asserts the attribute of the element
     */
    public void assertAttributeContainsValue(String element, String page, String attribute, String expectedValue, String... appendValueToXpath) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualValue = webElement.getAttribute(attribute);
            if (actualValue.contains(expectedValue) && webElement.isDisplayed())
                pass("Actual value [" + actualValue + "] is matching with expected value [" + expectedValue + "]");
            else
                throw new Exception("Actual value [" + actualValue + "] is not matching with expected value [" + expectedValue + "] or Element is not displaying");
        } catch (Exception e) {
            fail("Actual value [" + actualValue + "] is not matching with expected value [" + expectedValue + "]", e);
        }
    }

    /**
     * @param element
     * @param page
     * @param expectedValue
     * @throws Exception
     * @author: Lokesh Kumar
     * usage: Asserts the attribute of the element
     */
    public void assertElementContainsText(String element, String page, String expectedValue, String... appendValueToXpath) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");

            if (actualText.contains(expectedValue) && webElement.isDisplayed())
                pass("Actual Text [" + actualText + "] contains the expected text[" + expectedValue + "]");
            else
                throw new Exception("Actual Text [" + actualText + "] is not matching with expected text [" + expectedValue + "] or Element is not displaying");
        } catch (Exception e) {
            fail("Actual Text [" + actualText + "] is not matching with expected text [" + expectedValue + "]", e);
        }
    }

    /**
     * @param text
     * @throws Exception
     * @author: Lokesh KK
     */
    public void assertTextDisplayed(String text) throws Exception {
        try {
            waitUntilpageLoaded();
            waitUntilElementLoaded(driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")), 5);
            if (driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")).isDisplayed())
                pass("Test exists in page: " + text);
            else throw new Exception();
        } catch (Exception e) {
            fail("Text " + text + "doesn't exists in page", e);
        }
    }


    /**
     * @param text
     * @throws Exception
     * @author: Lokesh KK
     */
    public void assertTextDisplayed(String text, boolean trueOrFalse) throws Exception {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            if (trueOrFalse) {
                waitUntilpageLoaded();
                waitUntilElementLoaded(driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")), 5);
                if (driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")).isDisplayed())
                    pass("Text exists in page: " + text);
                else throw new Exception();
            } else {
                waitUntilpageLoaded();
                if (!driver.findElement(By.xpath("//*[normalize-space(text())=\"" + text + "\"]")).isDisplayed())
                    pass("Text not exists in page: " + text);
                else fail("Text exists in page", new Exception());
            }
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        } catch (Exception e) {
            if (!trueOrFalse)
                pass("Text not exists in page: " + text);
            else
                fail("Text doesn't exists in page", e);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }
    }

    /**
     * @throws Exception
     * @author: Lokesh KK
     */
    public void refresh() throws Exception {
        try {
            driver.navigate().refresh();
        } catch (Exception e) {
            fail("Failed to refresh page", e);
        }
    }

    /**
     * @throws Exception
     * @author: Lokesh KK
     */
    public void navigateBack() throws Exception {
        try {
            driver.navigate().back();
        } catch (Exception e) {
            fail("Failed to navigate back", e);
        }
    }

    /**
     * @param xpath
     * @param attribute
     * @param expectedValue
     * @throws Exception
     * @author: Lokesh KK
     */
    public void assertAttributeValue(String xpath, String attribute, String expectedValue) throws Exception {
        String actualValue = " ";
        try {
            WebElement webElement = driver.findElement(By.xpath(xpath));
            actualValue = webElement.getAttribute(attribute);
            if (actualValue.equals(expectedValue) && webElement.isDisplayed())
                pass("Actual value: [" + actualValue + "] of " + attribute + " is matching expected value [" + expectedValue + "]");
            else throw new Exception("Actual value is not matching with expected value or Element is not displaying");
        } catch (Exception e) {
            fail("Actual Value: [" + actualValue + "] is not matching with expected value [" + expectedValue + "] of " + xpath + "'s attribute" + attribute, e);
        }
    }

    /**
     * @param expected
     * @param actual
     * @throws Exception
     * @author Lokesh.kk
     */
    public <T> void assertEquals(T actual, T expected) throws Exception {
        try {
            if (expected != null) {
                if (!actual.equals(expected))
                    throw new Exception("Actual Value: [" + actual + "] is not Matching with expected value: [" + expected + "]");
                pass("Actual Text [" + actual.toString() + "] is matching with expected value [" + expected.toString() + "]");
            } else {
                if (!(actual == null))
                    throw new Exception("Actual Value: [" + actual + "] is not Matching with expected value: [" + expected + "]");
                pass("Actual Text [" + actual + "] is matching with expected value [" + null + "]");
            }
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    /**
     * @param expected
     * @param actual
     * @throws Exception
     * @author Lokesh.kk
     */
    public void assertContains(String expected, String actual) throws Exception {
        try {

            if (actual.contains(expected))
                pass("Actual Text [" + actual + "] is having  expected text [" + expected + "]");
            else
                throw new Exception("Actual Text [" + actual + "] is not containing the expected text [" + expected + "]");
        } catch (Exception e) {
            fail("Actual Text [" + actual + "] is not containing the expected text [" + expected + "]", e);
        }
    }


    /**
     * @param element
     * @param page
     * @throws Exception
     * @author: Lokesh Kumar
     * usage: Gets the text of the element
     */
    public String getText(String element, String page, String... appendValueToXpath) throws Exception {
        String actualText = " ";
        try {
            WebElement webElement = this.findElement(element, page, appendValueToXpath);
            actualText = webElement.getText();
            if (actualText.equals(" ") || actualText.equals(""))
                actualText = webElement.getAttribute("innerText");
            log("Text of an element " + element + ": " + actualText);
        } catch (Exception e) {
            fail("Get text failed for an element " + element, e);
        }
        return actualText;
    }

    /**
     * @author: Lokesh Kumar
     * returns the instance of driver
     */
    public WebDriver getWebDriver() {
        return driver;
    }

    /**
     * @param element
     * @param page
     * @author: Lokesh Kumar
     * Created on: 07/02/2020
     * Usage: Switches to frame which has elements
     */
    public void switchToFrameWhichHasElement(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            boolean flag = false;
            wait(4);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(7));
            frameSetMoveToElement = setMoveToElement;
            setMoveToElement = false;
            List<WebElement> iframes = driver.findElements(By.xpath("//iframe"));
            for (WebElement frame : iframes) {
                if (flag)
                    break;
                driver.switchTo().frame(frame);
                try {
                    this.findElement(element, page, appendValueToLocator);
                    flag = true;
                } catch (Exception e) {
                }
                List<WebElement> innerFrames = driver.findElements(By.xpath("//iframe"));
                if (innerFrames.size() > 0 & !flag) {
                    for (WebElement innerFrame : innerFrames) {
                        try {
                            driver.switchTo().frame(innerFrame);
                            this.findElement(element, page);
                            flag = true;
                            break;
                        } catch (Exception e) {
                        }
                    }
                }
                if (!flag)
                    this.switchToDefaultContent();
            }
            if (!flag)
                throw new Exception("Element not found " + element);
            pass("Switched to Frame which has " + element + " of " + page);


        } catch (Exception e) {
            fail("Failed to switch frame of" + element + " of " + page, e);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTimeout));
    }

    /**
     * @param by
     * @author: Lokesh Kumar
     * Created on: 07/02/2020
     * Usage: Switches to frame which has elements
     */
    public void switchToFrameWhichHasElement(By by) throws Exception {
        try {
            driver.switchTo().frame(driver.findElement(by));
            pass("Switched to Frame which has " + by + " element");
            frameSetMoveToElement = setMoveToElement;
            setMoveToElement = false;
        } catch (Exception e) {
            fail("Failed to switch frame of" + by + "element", e);
        }
    }

    /**
     * @param nameOrId
     * @throws Exception
     * @author: Lokesh Kumar
     * Created on: 07/02/2020
     */
    public void switchToFrame(String nameOrId) throws Exception {
        try {
            driver.switchTo().frame(nameOrId);
            pass("Switched to Frame: " + nameOrId);
            frameSetMoveToElement = setMoveToElement;
            setMoveToElement = false;
        } catch (Exception e) {
            fail("Failed to switch frame: " + nameOrId, e);
        }
    }

    /**
     * Usage: To switch from frames to default control
     *
     * @throws Exception
     * @author: Lokesh Kumar
     * Created on: 07/02/2020
     */
    public void switchToDefaultContent() throws Exception {
        try {
            driver.switchTo().defaultContent();
            setMoveToElement = frameSetMoveToElement;
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    /**
     * @param element
     * @param page
     * @throws Exception
     * @author Lokesh Kumar
     * Created on 07/02/2020
     * Usage: to hover on the given element
     */
    public void hover(String element, String page) throws Exception {
        try {
            WebElement webElement = this.findElement(element, page);
           /* Actions actions = new Actions(driver);
            actions.moveToElement(webElement).build().perform();*/

            ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", webElement);
            pass("Hovered on " + element + " on " + page);
        } catch (Exception e) {
            fail("Failed to hover on " + element + " in " + page, e);
        }
    }

    /**
     * @return Current page url
     * @throws Exception
     * @author Lokesh.kk
     * Usage: This method will return the url of th current page
     */
    public String getPageUrl() throws Exception {
        String url = driver.getCurrentUrl();
        log("Current page Url: " + url);
        return url;
    }

    /**
     * @throws Exception
     * @author Lokesh.kk
     * Usage: This method will click on provided link value
     */
    public void clickOnHyperLink(String linkText) throws Exception {
        try {
            waitUntilpageLoaded();
            WebElement element=driver.findElement(By.linkText(linkText));
            JavascriptExecutor je = (JavascriptExecutor) driver;
            je.executeScript("arguments[0].click()", element);
            pass("Clicked on: " + linkText + " link");
        } catch (Exception e) {
            fail("Failed to Click on " + linkText + " link ", e);
        }
    }

    /**
     * @throws Exception
     * @author Lokesh.kk
     * Usage: This method will asserts the dropdown list values
     */
    public void assertDropDownList(String element, String page, List<String> expectedDropDownOptions, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            List<WebElement> webElements = select.getOptions();
            List<String> actualDropDownOptions = new ArrayList<>();
            for (WebElement option : webElements) {
                actualDropDownOptions.add(option.getText());
            }
            if (expectedDropDownOptions.equals(actualDropDownOptions))
                pass("Provided Options " + expectedDropDownOptions.toString() + " are exists in element " + element + " in" + page);
            else
                throw new Exception("Provided Options are not exists in element " + element + " in" + page + "\n" +
                        "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    /**
     * @throws Exception
     * @author Lokesh.kk
     * Usage: This method will asserts the dropdown list values
     */
    public void assertDropDownList(String element, String page, List<String> expectedDropDownOptions, boolean retainOrder, String... appendValueToLocator) throws Exception {
        try {
            waitUntilpageLoaded();
            WebElement webElement = this.findElement(element, page);
            Select select = new Select(webElement);
            List<WebElement> webElements = select.getOptions();
            List<String> actualDropDownOptions = new ArrayList<>();
            for (WebElement option : webElements) {
                actualDropDownOptions.add(option.getText());
            }
            if (retainOrder) {
                if (expectedDropDownOptions.equals(actualDropDownOptions))
                    pass("Provided Options " + expectedDropDownOptions.toString() + " are exists in element " + element + " in" + page);
                else {
                    if (expectedDropDownOptions.containsAll(actualDropDownOptions))
                        throw new Exception("Provided Options are exists in element " + element + " in" + page + " but not in Order" + "\n" +
                                "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);
                    else
                        throw new Exception("Provided Options are not exists in element " + element + " in" + page + "\n" +
                                "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);
                }
            } else {
                if (expectedDropDownOptions.containsAll(actualDropDownOptions))
                    pass("Provided Options " + expectedDropDownOptions.toString() + " are exists in element " + element + " in" + page);
                else
                    throw new Exception("Provided Options are not exists in element " + element + " in" + page + "\n" +
                            "Expected Options: " + expectedDropDownOptions + "\n Actual Options: " + actualDropDownOptions);

            }
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    /**
     * @throws Exception
     * @author Lokesh.kk
     * Usage: This method will return the selected dropdown list value
     */
    public String getSelectedOptionFromDropDownList(String element, String page, String... appendValueToLocator) throws Exception {
        String option = "";
        try {
            waitUntilpageLoaded();
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            Select select = new Select(webElement);
            option = select.getFirstSelectedOption().getText();
            log("Selected option of Element: " + element + " is " + option);
        } catch (Exception e) {
            fail("Failed to get selected option: " + option + " of Element: " + element, e);
        }
        return option;
    }

    /**
     * @param element
     * @param page
     * @param appendValueToLocator
     * @throws Exception
     * @author Lokesh.kk
     * usage: clicks only if elements present on page
     */
    public void clickIfElementPresent(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            boolean flag = false;
            waitUntilpageLoaded();
            int size = this.getWebElementsSize(element, page, appendValueToLocator);
            if (size > 0) {
                if (this.isDisplayed(element, page, appendValueToLocator) &&
                        this.isEnabled(element, page, appendValueToLocator)) {
                    this.javaScriptExecutorClick(element, page, appendValueToLocator);
                    flag = true;
                }
            }
            if (flag)
                pass("Clicked on element: " + element + " in " + page);
            else
                pass("Element: " + element + " not exist in " + page);
        } catch (Exception e) {
            fail("Failed to click on Element: " + element + " in " + page, e);
        }
    }

    /**
     * @param element
     * @param page
     * @param appendValueToLocator
     * @return boolean
     * @throws Exception
     * @author Lokesh.kk
     * usage: returns boolean value for element display status
     */
    public boolean isDisplayed(String element, String page, String... appendValueToLocator) throws Exception {
        boolean flag = false;
        try {
            waitUntilpageLoaded();
            flag = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator))
                    .isDisplayed();
            if (flag)
                log("Element " + element + " is displaying in " + page);
            else
                log("Element " + element + " is not displaying in " + page);
        } catch (Exception e) {
            log("Element " + element + " is not displaying in " + page);
        }
        return flag;
    }

    /**
     * @param element
     * @param page
     * @param appendValueToLocator
     * @return boolean
     * @throws Exception
     * @author Lokesh.kk
     * usage: returns boolean value for element display status
     */
    public boolean isSelected(String element, String page, String... appendValueToLocator) throws Exception {
        boolean flag = false;
        try {
            waitUntilpageLoaded();
            flag = driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page, appendValueToLocator))
                    .isSelected();
            if (flag)
                log("Element " + element + " is selected in " + page);
            else
                log("Element " + element + " is not selected in " + page);
        } catch (Exception e) {
            log("Element " + element + " is not selected in " + page);
        }
        return flag;
    }

    /**
     * @param element
     * @param page
     * @param appendValueToLocator
     * @return
     * @throws Exception
     * @author Lokesh.kk
     */
    public void assertImageLoaded(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            HttpGet request = new HttpGet(webElement.getAttribute("src"));
            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            boolean flag = responseCode >= 200 && responseCode <= 299;
            if (flag)
                pass("Image Loaded Successfully: " + element + " in Page: " + page);
            else
                throw new Exception("Image not Loaded: " + element + " in Page: " + page);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    /**
     * @param element
     * @param page
     * @param appendValueToLocator
     * @return boolean
     * @throws Exception
     * @author Lokesh.kk
     * usage: returns boolean value for element enabled status
     */
    public boolean isEnabled(String element, String page, String... appendValueToLocator) throws Exception {
        boolean flag = false;
        try {
            waitUntilpageLoaded();
            flag = this.findElement(element, page, appendValueToLocator).isEnabled();
            if (flag)
                log("Element " + element + " is enabled in " + page);
            else
                log("Element " + element + " is not enabled in " + page);
        } catch (Exception e) {
            log("Element " + element + " is not enabled in " + page);
        }
        return flag;
    }

    /**
     * @param url
     * @throws Exception
     * @author: Lokesh.kk
     */
    public void openNewWindow(String... url) throws Exception {
        String launchUrl = "";
        if (url.length > 0)
            launchUrl = url[0];
        try {
            ((JavascriptExecutor) driver).executeScript("window.open(arguments[0])", launchUrl);
            pass("Launched url in new Window: " + launchUrl);
        } catch (Exception e) {
            fail("Failed to Launch new Window with Url: " + launchUrl, e);
        }
    }

    /**
     * @param message
     * @throws Exception
     * @author Lokesh.kk
     * usage is to make test fail
     */
    public void assertFail(String message) throws Exception {
        fail(message, new Exception(message));
    }

    /**
     * @param message
     * @throws IOException
     * @author Lokesh.kk
     * usage is to make a step to pass
     */
    public void assertPass(String message) throws IOException {
        pass(message);
    }

    /**
     * @throws Exception
     * @author Lokesh.kk
     * Usage: This method will make thread to sleep for given time
     */
    public void wait(int seconds) throws InterruptedException {
        long time = seconds * 1000;
        Thread.sleep(time);
    }

    /**
     * @author: Lokesh Kumar
     * Created on: 07/02/2020
     * usage: CLoses the browser instances and
     */
    public void exit() {
        if (quitDriverOnEndOfTest) {
            for (WebDriver driver : drivers) {
                driver.quit();
            }
        }
        extentReports.flush();
    }

    /**
     * @author: Lokesh Kumar
     */
    public void afterTest() {
        System.out.println("-----------------------------Tests Completed-----------------------------");
    }

    /**
     * @author: Lokesh Kumar
     */

    public void extentSetup() {
        String date = ((new SimpleDateFormat("ddMMMyyHHmmss")).format(new Date()));
        Map<String, String> map = (Map) System.getProperties();
        if (extentHtmlReporter == null)
            extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports\\Automation Summary Report " + date + ".html");
        ExtentReporterUtils.setExtentHtmlReporter(extentHtmlReporter);
        if (extentReports == null)
            extentReports = new ExtentReports();
        ExtentReporterUtils.setExtentReports(extentReports);
        extentReports.attachReporter(extentHtmlReporter);
        extentHtmlReporter.config().setTheme(Theme.STANDARD);
        extentHtmlReporter.config().setDocumentTitle("Automation Reports");
        extentHtmlReporter.config().setReportName("IBusiness Software Selenium Test Reports");
        extentReports.setSystemInfo("Os", map.get("os.name"));
        extentReports.setSystemInfo("User", map.get("user.name"));
        extentReports.setSystemInfo("Java Version", map.get("java.version"));
        extentReports.setSystemInfo("Environment", StartTest.getEnv());
        System.out.println("------------------------------Tests Started------------------------------");
    }


    /**
     * @author: lokesh.kk
     */
    public void acceptAlert() throws Exception {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            pass("Accepted popup Alert");
        } catch (Exception e) {
            fail("Failed to accept alert", e);
        }
    }

    /**
     * @author: lokesh.kk
     */
    public void declineAlert() throws Exception {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            pass("Declined popup Alert");
        } catch (Exception e) {
            fail("Failed to decline alert", e);
        }
    }

    /**
     * @author: lokesh.kk
     */
    public String getAlertText() throws Exception {
        boolean flagM = setMoveToElement;
        boolean flagS = StartTest.setScreenshotforEachStep;
        setMoveToElement = false;
        StartTest.setScreenshotforEachStep = false;

        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            pass("Alert text is:" + alertText);
            setMoveToElement = flagM;
            StartTest.setScreenshotforEachStep = flagS;
            return alertText;
        } catch (Exception e) {

            fail("Failed to get alert text", e);
        }
        return null;
    }

    /**
     * @author: lokesh.kk
     */
    public void setAlertText(String text) throws Exception {
        try {
            Alert alert = driver.switchTo().alert();
            alert.sendKeys(text);
            pass("Alert text set to:" + text);
        } catch (Exception e) {
            fail("Failed to set alert text: " + text, e);
        }
    }

    public boolean isAlertPresent() throws Exception {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        } catch (Exception e) {
            fail("Failed due to alert", e);
            return false;
        }

    }

    public void scrollDown(int pixels) throws Exception {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(" + String.valueOf(pixels) + ", document.body.scrollHeight)");
        } catch (Exception e) {
            fail("Failed to scroll down", e);
        }
    }

    public void scrollByArrow(int noOfTimes) throws Exception {
        try {
            for (int i = 0; i < noOfTimes; i++) {
                driver.findElement(By.cssSelector("body")).sendKeys(Keys.ARROW_DOWN);
            }
        } catch (Exception e) {
            fail("Failed to scroll down", e);
        }
    }

    public void scrollToElement(String element, String page, String... appendValueToLocator) throws Exception {
        try {
            waitUntilElementVisible(element, page, waitTimeout, appendValueToLocator);
            WebElement webElement = this.findElement(element, page, appendValueToLocator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", webElement);

            pass("Scrolled to element: " + element + " in " + page);
        } catch (Exception e) {
            fail("Failed to scroll to " + element + " in " + page, e);
        }
    }

}
