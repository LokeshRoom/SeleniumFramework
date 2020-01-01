import driverfactory.Browsers;
import operations.Operation;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;
import testngrunner.StartTest;

public class Tests {

    public Tests() throws Exception {
    }

    StartTest startTest = new StartTest(Browsers.FIREFOX, "QA", "Objects.yaml");

    @Test(description = "Launching Google Home page")
    public void google_Search() throws Exception {

        Operation operation = startTest.startTest();
        operation.goToUrl("Google_HomepageURL");
        operation.enterText("txt_SearchBox", "Google_HomePage", "Lokesh Kumar K");
        operation.enterText("txt_SearchBox", "Google_HomePage", Keys.ENTER);
        operation.quit();

    }

    @Test
    public void likedIn_Search() throws Exception {
        Operation operation = startTest.startTest();
        operation.goToUrl("LinkedIN_HomepageURL");
        //operation.click("btn_SearchBox", "LinkedIN_Homepage");
        operation.click("btn_People", "LinkedIN_Homepage");
        operation.enterText("txt_FirstName", "LinkedIN_Homepage", "Lokesh Kumar");
        operation.enterText("txt_LastName", "LinkedIN_Homepage", "K");
        operation.click("btn_SearchPeople", "LinkedIN_Homepage");
        operation.quit();
    }

    @Test
    public void test() {
        long l = (long) -Math.pow(2, 63);
        System.out.println(Math.pow(2, 63));
    }
}
