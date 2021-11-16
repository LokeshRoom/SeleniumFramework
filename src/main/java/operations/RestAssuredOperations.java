package operations;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import objectutils.ObjectReader;
import operations.ExtentReporterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.ITestListener;
import testngrunner.StartTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Creator: Lokesh.kk
 * Created on: 2/17/2020
 **/
public class RestAssuredOperations extends ObjectReader implements ITestListener {
    private static Logger logger = LogManager.getLogger("RestAssuredOperation");
    ExtentHtmlReporter extentHtmlReporter = ExtentReporterUtils.getExtentHtmlReporterAPI();
    ExtentReports extentReports = ExtentReporterUtils.getExtentReportsAPI();
    ExtentTest extentTest;
    RequestSpecification requestSpecification;
    boolean requiredHeadersLog = false;

    public RestAssuredOperations(String host, String endpoint, String... testDescription) {
        if (extentReports == null)
            extentSetup();
        requestSpecification = given();
        requestSpecification.baseUri(host);
        requestSpecification.basePath(endpoint);
        if (testDescription.length > 0)
            extentTest = extentReports.createTest(testDescription[0]);
        else
            extentTest = extentReports.createTest(Thread.currentThread().getStackTrace()[3].getMethodName());
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\log4j2.xml");
        context.setConfigLocation(file.toURI());
    }

    public Response get() throws Exception {
        try {
            Response response = requestSpecification.get();
            pass("Get request successfully submitted: " + response.asString());
            log("Response Code: " + response.getStatusCode());
            log("Response Status Line: " + response.getStatusLine());
            if (requiredHeadersLog)
                    log("Response Headers: " + response.getHeaders().toString());
            log("Response Body: " + response.getBody().asString());
            return response;
        }
        catch (Exception e){
            fail("Get request failed with Exception: ",e);
            return null;
        }
    }

    public Response get(URI uri) throws Exception {
        try{
            Response response=requestSpecification.get(uri);
            pass("Get request succesfully submitted: "+response.asString());
            log("Response Code: "+response.getStatusCode());
            log("Response Status Line: "+response.getStatusLine());
            if (requiredHeadersLog)
                log("Response Headers: " + response.getHeaders().toString());
            log("Response Body: "+response.getBody().asString());
            return response;}
        catch (Exception e){
            fail("Get request failed with Exception: ",e);
            return null;
        }
    }

    public RequestSpecification addHeader(String headerKey,String headerValue) throws Exception {
        try{
            requestSpecification.header(headerKey,headerValue);
            log("Header Added: "+headerKey+" :"+headerValue);
            return requestSpecification;}
        catch (Exception e){
            fail("Failed to add header: "+headerKey+ ": "+headerKey+" with Exception: ",e);
            return null;
        }
    }

    public RequestSpecification addHeaders(HashMap<String,String> headers) throws Exception {
        try{
            requestSpecification.headers(headers);
            log("Headers Added: "+ headers.toString());
            return requestSpecification;}
        catch (Exception e){
            fail("Failed to add headers with Exception: "+headers.toString()+"\n",e);
            return null;
        }
    }

    public RequestSpecification setBody(String body) throws Exception {
        try{
            requestSpecification.body(body);
            log("Request Body Added: "+ body);
            return requestSpecification;}
        catch (Exception e){
            fail("Failed to set request body with Exception: "+body+"\n",e);
            return null;
        }
    }

    public RequestSpecification setParameter(String paramKey,String paramValue) {
        return requestSpecification.param(paramKey,paramValue);
    }

    public RequestSpecification setParameters(Map<String,String> params) {
        return requestSpecification.params(params);
    }

    public Response post() throws Exception {
        try{
            Response response = requestSpecification.post();
            pass("Post request is successful: \n" + response.asString());
            log("Response Code: " + response.getStatusCode());
            log("Response Status Line: "+response.getStatusLine());
            if (requiredHeadersLog)
                log("Response Headers: " + response.getHeaders().toString());
            log("Response Body: "+response.getBody().asString());
            return response;
        }
        catch (Exception e){
            fail("Post request failed: ",e);
            throw new Exception(e);
        }
    }

    public Response post(URI uri) throws Exception {
        try {
            Response response = requestSpecification.post(uri);
            pass("Post request is successful: \n" + response.asString());
            return response;
        } catch (Exception e) {
            fail("Post request failed: ", e);
            throw new Exception(e);
        }
    }

    public Response patch(URI uri) throws Exception {
        try {
            Response response = requestSpecification.patch(uri);
            pass("Patch request is successful: \n" + response.asString());
            return response;
        } catch (Exception e) {
            fail("Patch request failed: ", e);
            throw new Exception(e);
        }
    }

    public Response patch() throws Exception {
        try {
            Response response = requestSpecification.patch();
            pass("Patch request is successful: \n" + response.asString());
            return response;
        } catch (Exception e) {
            fail("Patch request failed: ", e);
            throw new Exception(e);
        }
    }

    public void assertStatusCode(Response response, int expectedResponseCode) throws Exception {
        if (response.getStatusCode() == expectedResponseCode) {
            pass("Expected status code '" + expectedResponseCode + "' is matching with Actual code '" +
                    response.getStatusCode() + "'");
        } else {
            fail("Expected status code '" + expectedResponseCode + "' is not matching with Actual code '" +
                    response.getStatusCode(), new Exception());
        }
    }

    public void assertResponseBody(Response response,String expectedResponseBody) throws Exception {
        if(response.getBody().asString().equals(expectedResponseBody)){
            pass("Expected Response body '"+expectedResponseBody+"' is matching with Actual response body '"+
                    response.getBody().asString());
        }else {
            fail("Expected Response body '"+expectedResponseBody+"' is not matching with Actual response body '"+
                    response.getBody().asString(),new Exception());
        }

    }

    public void assertResponseBodyContainsText(Response response,String expectedResponseBody) throws Exception {
        if(response.getBody().asString().contains(expectedResponseBody)){
            pass("Expected Response body '"+expectedResponseBody+"' content matching with Actual response body '"+
                    response.getBody().asString()+"'");
        }else {
            fail("Expected Response body '"+expectedResponseBody+"' doesn't contains in Actual response body '"+
                    response.getBody().asString()+"'",new Exception());
        }

    }

    public void extentSetup() {
        String date = ((new SimpleDateFormat("ddMMMyyHHmmss")).format(new Date()));
        Map<String, String> map = (Map) System.getProperties();
        if (extentHtmlReporter == null)
            extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\API Reports\\API Automation Summary Report " + date + ".html");
        ExtentReporterUtils.setExtentHtmlReporterAPI(extentHtmlReporter);
        if (extentReports == null)
            extentReports = new ExtentReports();
        ExtentReporterUtils.setExtentReportsAPI(extentReports);
        extentReports.attachReporter(extentHtmlReporter);
        extentHtmlReporter.config().setTheme(Theme.STANDARD);
        extentHtmlReporter.config().setDocumentTitle("Rest Assured Automation Reports");
        extentHtmlReporter.config().setReportName("IBusiness Software Rest Assured Test Reports");
        extentReports.setSystemInfo("Os", map.get("os.name"));
        extentReports.setSystemInfo("User", map.get("user.name"));
        extentReports.setSystemInfo("Java Version", map.get("java.version"));
        extentReports.setSystemInfo("Environment", StartTest.getEnv());
        System.out.println("------------------------------Tests Started------------------------------");
    }

    void pass(String message) throws IOException {
        extentTest.pass(message);
        logger.info(message);
    }

    void log(String message) throws IOException {
        extentTest.info(message);
        logger.info(message);
    }

    /**
     * @param message
     * @param e
     * @throws Exception
     * @author Lokesh.kk
     */
    void fail(String message, Exception e) throws Exception {
        extentTest.fail(message + " " + e);
        logger.info(message + " " + e);
        throw new Exception(e);

    }
    public void exit() {
        extentReports.flush();
    }

    public void assertJsonSchema(Response response,String fileName) throws Exception {
        try{
            File file = new File(System.getProperty("user.dir") + "\\src\\test\\resources\\Json_Schemas\\"+fileName+"");
            JSONObject jsonObject=new JSONObject(
                    new JSONTokener(new FileInputStream(file.getAbsolutePath()))
            );

            JSONObject jsonSubject = new JSONObject(response.getBody().asString());
            Schema schema = SchemaLoader.load(jsonObject);
            schema.validate(jsonSubject);
            pass("Schema validated successfully");}
        catch (FileNotFoundException e){
            fail("Schema file not found",e);
        }
        catch (Exception e){
            fail("Schema validation failed with Exception: ",e);
        }
    }
}
