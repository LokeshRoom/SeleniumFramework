package testngrunner;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Creator: Lokesh.kk
 **/
public class RetryExecutor implements IRetryAnalyzer {

    private static int maxRetryCount = 0;
    private int retryCount = 0;

    {
        try {
            maxRetryCount = Integer.parseInt(System.getProperty("retrycount"));
        } catch (Exception e) {
        }
    }


    /**
     * @param result
     * @author: Lokesh.kk
     * usage: to execute the failed test scenarios
     */
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }
}
