package testngrunner;

import com.aventstack.extentreports.ExtentReports;
import driverfactory.Browsers;
import io.github.bonigarcia.wdm.WebDriverManager;
import operations.Operation;


public class StartTest {

    private static Browsers browser;
    private static String env;
    private static String yamlFile;

    public static ExtentReports getExtentReports() {
        return extentReports;
    }

    public static void setExtentReports(ExtentReports extentReports) {
        StartTest.extentReports = extentReports;
    }

    private static ExtentReports extentReports;

    public Operation getOperation() {
        return operation;
    }

    private static Operation operation;

    public StartTest(Browsers browser, String env, String yamlFile) throws Exception {
        System.out.println(System.getProperty("browser"));
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
                StartTest.browser = Browsers.valueOf((System.getProperty("browser").toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new Exception("wrong.browser.exception: Please provide correct browser value from global variable\n" + e);
            }
        }
        if (browser == null) {
            throw new Exception("wrong.browser.exception: Please provide browser value");
        }
        if (StartTest.browser.equals(Browsers.CHROME)) {
            WebDriverManager.chromedriver().setup();
        } else if (StartTest.browser.equals(Browsers.IE)) {
            WebDriverManager.iedriver().setup();
        } else if (StartTest.browser.equals(Browsers.FIREFOX)) {
            WebDriverManager.firefoxdriver().setup();
        } else if (StartTest.browser.equals(Browsers.EDGE)) {
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

}





