
import DriverFactory.Browsers;
import Operations.Operation;
import TestNGRunner.StartTest;
import org.openqa.selenium.Keys;

//@Listeners({StartTest.class})

public class Test {
    private StartTest startTest = new StartTest(Browsers.FIREFOX, "QA", "Objects.yaml");

    public Test() throws Exception {
    }

    @org.testng.annotations.Test(description = "Launching Google Home page")
    public void main() throws Exception {

        System.out.println("Browser: " + StartTest.getBrowser());
        System.out.println("Environment: " + StartTest.getEnv());
        Operation operation = startTest.getOperation();
        operation.goToUrl("Google_HomepageURL");
        operation.enterText("txt_SearchBox", "Google_HomePage", "Lokesh Kumar K");
        operation.enterText("txt_SearchBox", "Google_HomePage", Keys.ENTER);

        operation.exit();

    }
}
