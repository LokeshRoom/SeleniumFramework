
import DriverFactory.Browsers;
import TestNGRunner.StartTest;

//@Listeners({StartTest.class})

public class Test {
    StartTest startTest = new StartTest(Browsers.CHROME, "QA", "Objects.yaml");

    public Test() throws Exception {
    }

    @org.testng.annotations.Test
    public void main() {
        System.out.println(StartTest.getBrowser());
        System.out.println(StartTest.getEnv());
    }
}
