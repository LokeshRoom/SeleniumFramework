package TestNGRunner;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;


public class StartTest {
    private ExtentReports extentReports;
    private String browser;
    private String environment;

    public StartTest(String browser, String environment) {
        setBrowser(browser);
        setEnvironment(environment);
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @BeforeSuite
    public void settingConfiguration() {
        ExtentReports extentReports = new ExtentReports();
        this.extentReports = extentReports;
        ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports");
        extentReports.attachReporter(extentHtmlReporter);
    }

    @BeforeMethod
    public void setUp() {
        if (!(System.getProperty("browser").isEmpty() || System.getProperty("browser") == null)) {
            this.browser = System.getProperty("browser");

            if (browser.equalsIgnoreCase("chrome")) {
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir").concat("\\Drivers\\chromedriver.exe"));
            } else if (browser.equalsIgnoreCase("ie")) {
                System.setProperty("webdriver.ie.driver", System.getProperty("user.dir").concat("\\Drivers\\IEDriverServer.exe"));
            } else if (browser.equalsIgnoreCase("firefox")) {
                System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir").concat("\\Drivers\\geckodriver.exe"));
            } else new Exception("wrong.browser.exception: Please provide correct browser value");
        } else if (!(browser == null || browser.isEmpty())) {
            if (browser.equalsIgnoreCase("chrome")) {
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir").concat("\\Drivers\\chromedriver.exe"));
            } else if (browser.equalsIgnoreCase("ie")) {
                System.setProperty("webdriver.ie.driver", System.getProperty("user.dir").concat("\\Drivers\\IEDriverServer.exe"));
            } else if (browser.equalsIgnoreCase("firefox")) {
                System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir").concat("\\Drivers\\geckodriver.exe"));
            } else new Exception("wrong.browser.exception: Please provide correct browser value");

        }


    }
}

