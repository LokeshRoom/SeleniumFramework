package operations;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class ExtentReporterUtils {


    private static ExtentHtmlReporter extentHtmlReporter;

    private static ExtentReports extentReports;

    private static ExtentHtmlReporter extentHtmlReporterAPI;

    private static ExtentReports extentReportsAPI;


    public static ExtentHtmlReporter getExtentHtmlReporter() {
        return extentHtmlReporter;
    }

    public static void setExtentHtmlReporter(ExtentHtmlReporter extentHtmlReport) {
        if (extentHtmlReporter == null)
            extentHtmlReporter = extentHtmlReport;
    }

    public static ExtentReports getExtentReports() {
        return extentReports;
    }

    public static void setExtentReports(ExtentReports extentReport) {
        if (extentReports == null)
            extentReports = extentReport;
    }


    public static ExtentHtmlReporter getExtentHtmlReporterAPI() {
        return extentHtmlReporterAPI;
    }

    public static void setExtentHtmlReporterAPI(ExtentHtmlReporter extentHtmlReportAPI) {
        if (extentHtmlReporterAPI == null)
            extentHtmlReporterAPI = extentHtmlReportAPI;
    }

    public static ExtentReports getExtentReportsAPI() {
        return extentReportsAPI;
    }

    public static void setExtentReportsAPI(ExtentReports extentReportAPI) {
        if (extentReportsAPI == null)
            extentReportsAPI = extentReportAPI;
    }
}
