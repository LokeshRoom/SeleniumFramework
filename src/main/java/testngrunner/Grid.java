package testngrunner;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.net.URL;

/**
 * Creator: Lokesh.kk
 * Created on: 2/6/2020
 **/
public class Grid {


    public static void main(String[] args) throws Exception {
        //System.setProperty("webdriver.chrome.driver","D:\\Eclipse\\Workspace\\SeleniumFramework-master\\src\\test\\chromedriver.exe");
        //WebDriverManager.iedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("--headless");
        options.setCapability("webdriver.chrome.driver", "\\src\\test\\chromedriver.exe");
        URL url = new URL("http://172.20.3.37:4444/wd/hub");
        RemoteWebDriver driver = new RemoteWebDriver(url, options);
        driver.get("https://google.com");
        System.out.println(driver.getTitle());
        FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE),new File("scrst.png"));
        driver.quit();
    }
}
