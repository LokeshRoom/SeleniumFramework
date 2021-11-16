package testngrunner;

import driverfactory.Browsers;
import io.github.bonigarcia.wdm.WebDriverManager;
import operations.Operation;
import operations.RestAssuredOperations;


public class StartTest {

    public static boolean setScreenshotforEachStep = false;
    private static Browsers browser;
    private static String env;
    private static String yamlFile;


    public StartTest(Browsers browser, String env, String yamlFile, boolean setScreenshotEachStep) {
        try {
            setBrowser(browser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setEnv(env);
        setYamlFile(yamlFile);
        if (setScreenshotEachStep)
            setScreenshotforEachStep = true;
    }
    public StartTest(){}

    public static Browsers getBrowser() {
        return browser;
    }

    public static void setBrowser(Browsers browser) throws Exception {
        try {
            if (System.getProperty("browser").equals("") || System.getProperty("browser") == null)
                StartTest.browser = browser;
            else {
                try {
                    StartTest.browser = Browsers.valueOf((System.getProperty("browser").toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new Exception("wrong.browser.exception: Please provide correct browser value from global variable\n" + e);
                }
            }
        } catch (NullPointerException e) {
            StartTest.browser = browser;
        }
        if (browser == null) {
            throw new Exception("wrong.browser.exception: Please provide browser value");
        }
        switch (StartTest.browser) {
            case CHROME:
            case CHROME_HEADLESS:
                WebDriverManager.chromedriver().setup();
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                break;
            case EDGE:
                WebDriverManager.edgedriver().arch32();
                WebDriverManager.edgedriver().setup();
                break;
            case IE:
                WebDriverManager.iedriver().arch32();
                WebDriverManager.iedriver().setup();
                break;
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

    public Operation launchAnotherBrowser(Operation operation, Browsers... browser) throws Exception {
        Browsers browserActual = StartTest.browser;
        if (browser.length > 0) {
            if (System.getProperty("browser") == null)
                browserActual = browser[0];
            switch (browserActual) {
                case CHROME:
                case CHROME_HEADLESS:
                    WebDriverManager.chromedriver().setup();
                    break;
                case FIREFOX:
                    WebDriverManager.firefoxdriver().setup();
                    break;
                case EDGE:
                    WebDriverManager.edgedriver().arch32();
                    WebDriverManager.edgedriver().setup();
                    break;
                case IE:
                    WebDriverManager.iedriver().arch32();
                    WebDriverManager.iedriver().setup();
                    break;
            }
        }


        return new Operation(browserActual, operation);
    }

    public Operation startTest(String... testDescriptionForReports) throws Exception {
        return new Operation(StartTest.browser, testDescriptionForReports);
    }

    public Operation startTest(boolean debug, String port, String... testDescriptionForReports) throws Exception {
        return new Operation(port, testDescriptionForReports);
    }

    public RestAssuredOperations startAPITest(String host, String endpoint, String... testDescriptionForReports) throws Exception {
        return new RestAssuredOperations(host, endpoint, testDescriptionForReports);
    }

}





