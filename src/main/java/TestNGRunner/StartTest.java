package TestNGRunner;

import DriverFactory.Browsers;
import Operations.Operation;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class StartTest implements ITestListener {

    private static Browsers browser;
    private static String env;
    private static String yamlFile;
    private static ExtentReports extentReports;

    public Operation getOperation() {
        return operation;
    }

    private static Operation operation;

    public StartTest(Browsers browser, String env, String yamlFile) throws Exception {
        setBrowser(browser);
        setEnv(env);
        setYamlFile(yamlFile);
        operation = new Operation();

    }

    public static Browsers getBrowser() {
        return browser;
    }

    public static void setBrowser(Browsers browser) throws Exception {
        if ((System.getProperty("browser") == null))
            StartTest.browser = browser;
        else {
            try {
                StartTest.browser = Browsers.valueOf(System.getProperty("browser").toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new Exception("wrong.browser.exception: Please provide correct browser value from global variable");
            }
        }
        if (browser == null) {
            throw new Exception("wrong.browser.exception: Please provide browser value");
        }
        if (browser.equals(Browsers.CHROME)) {
            WebDriverManager.chromedriver().setup();
        } else if (browser.equals(Browsers.IE)) {
            WebDriverManager.iedriver().setup();
        } else if (browser.equals(Browsers.FIREFOX)) {
            WebDriverManager.firefoxdriver().setup();
        } else if (browser.equals(Browsers.EDGE)) {
            WebDriverManager.edgedriver().setup();
        }
    }

    public static String getEnv() {
        return env;
    }

    public static void setEnv(String env) {
        StartTest.env = env;
    }

    public static String getYamlFile() {
        return yamlFile;
    }

    public static void setYamlFile(String yamlFile) {
        StartTest.yamlFile = yamlFile;
    }


    @Override

    public void onTestStart(ITestResult result) {
        System.out.println("Test Started: " + result.getTestName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {

    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
        ExtentReports extentReports = new ExtentReports();
        StartTest.extentReports = extentReports;
        ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports");
        extentReports.attachReporter(extentHtmlReporter);
        System.out.println("------Test Started-----------");
    }

    @Override
    public void onFinish(ITestContext context) {
        extentReports.flush();
    }


}





