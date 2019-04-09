package Operations;


import ObjectUtils.ObjectReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Operation extends ObjectReader {

    private WebDriver driver;
    private JSONObject yamlJsonObject;
    private ExtentReports extentReports = new ExtentReports();
    private ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter("C:\\Users\\PlayGround\\IdeaProjects\\SeleniumProject\\Reports\\Reports.html");
    private ExtentTest extentTest;

    public Operation() throws Exception {
        System.setProperty("webdriver.driver.chromedriver", "C:\\Users\\PlayGround\\IdeaProjects\\SeleniumProject\\chromedriver.exe");
        driver = new ChromeDriver();
        extentReports.attachReporter(extentHtmlReporter);
        extentTest = extentReports.createTest("My Test Case Name");
        this.setYamlJsonObject("Objects.yaml", "C:\\Users\\PlayGround\\IdeaProjects\\SeleniumProject\\");

    }

    public JSONObject getYamlJsonObject() {
        return yamlJsonObject;
    }

    public void setYamlJsonObject(String fileName, String path) throws Exception {
        this.yamlJsonObject = ObjectReader.readYaml(fileName, path);
    }

    private WebElement findElement(String element, String page) {
        return driver.findElement(ObjectReader.getElement(yamlJsonObject, element, page));
    }

    public void goToUrl(String urlObject) {
        String url = ObjectReader.getUrl(yamlJsonObject, urlObject);
        driver.get(url);
        extentTest.log(Status.PASS, "Launched Url: " + url);

    }

    public void navigateToUrl(String url) {
        driver.navigate().to(url);
        extentTest.log(Status.PASS, "Navigate to URL: " + url);
    }

    public void enterText(String element, String page, CharSequence text) throws Exception {
        WebElement webElement = this.findElement(element, page);
        webElement.sendKeys(text);
        Robot robot = new Robot();
        BufferedImage bufferedImage = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String scrst = screenshot.getScreenshotAs(OutputType.BASE64);

        extentTest.log(Status.PASS, "Enterted Text: " + text);

        extentTest.addScreenCaptureFromBase64String(bufferedImage.toString());
    }

    public void click(String element, String page) {
        WebElement webElement = this.findElement(element, page);
        webElement.click();
        extentTest.log(Status.PASS, "Clicked on element: " + element);
        extentReports.flush();
    }

    @AfterMethod
    public void exit() {
        driver.quit();
        extentReports.flush();

    }

}
