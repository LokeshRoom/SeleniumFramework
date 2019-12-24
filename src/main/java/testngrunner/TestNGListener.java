package testngrunner;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNGListener implements ITestListener {
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
        StartTest.setExtentReports(extentReports);
        ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\Reports");
        extentReports.attachReporter(extentHtmlReporter);
        System.out.println("------Test Started-----------");
    }

    @Override
    public void onFinish(ITestContext context) {
        StartTest.getExtentReports().flush();
    }

}
