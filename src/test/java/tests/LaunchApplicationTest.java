package tests;

import helpers.Helpers;
import helpers.PropertiesReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class LaunchApplicationTest {

    protected IOSDriver<IOSElement> driver = null;
    protected DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    protected WebDriverWait wait = null;

    protected Helpers helper;

    protected String speedIndex = null;
    protected String cpuAvg = null;
    protected String cpuMax = null;
    protected String memAvg = null;
    protected String memMax = null;
    protected String batteryAvg = null;
    protected String batteryMax = null;

    @BeforeMethod
    public void setUp(ITestContext context, Method method) throws MalformedURLException {
        String deviceQuery = context.getCurrentXmlTest().getParameter("deviceQuery");

        desiredCapabilities.setCapability("testName", method.getName());
        desiredCapabilities.setCapability("accessKey", new PropertiesReader().getProperty("accessKey"));
        desiredCapabilities.setCapability("deviceQuery", deviceQuery);
        desiredCapabilities.setCapability("autoAcceptAlerts", true);

        driver = new IOSDriver<>(new URL(new PropertiesReader().getProperty("cloudUrl")), desiredCapabilities);
        wait = new WebDriverWait(driver, 10);
        helper = new Helpers(driver);
    }

    @Test
    @Parameters({"nvProfile", "captureLevel"})
    public void test_launch_of_application_response(String nvProfile, String captureLevel, @Optional Method method) {

        try {
            // Start a group that will contain the individual test steps until 'endGroupingOfSteps' is called
            helper.startGroupingOfSteps(method.getName());

            // Install the Application only
            driver.executeScript("seetest:client.install(\"cloud:com.experitest.ExperiBank\", \"false\", \"false\")");

            // Start Performance Transaction Capturing
            helper.startCapturePerformanceMetrics(nvProfile, captureLevel, "com.experitest.ExperiBank");

            // Click on the EriBank icon on the Device Home Page
            driver.findElement(By.xpath("//XCUIElementTypeIcon[@name='SeeTestDemoApp']")).click();

            // Verify user landed on the Login page
            wait.until(ExpectedConditions.elementToBeClickable(By.name("usernameTextField")));

            // End the Performance Transaction Capturing
            String response = helper.endCapturePerformanceMetrics(method.getName());

            // Ends the group that was started by 'startGroupingOfSteps'. In the Report we can now expand a group to see a set of steps within the group
            helper.endGroupingOfSteps();

            // Extract relevant properties from the Performance Transaction Response
            String link = helper.getPropertyFromPerformanceTransactionReport(response, "link");
            // Extract relevant properties from the Performance Transaction Response
            String transactionId = helper.getPropertyFromPerformanceTransactionReport(response, "transactionId");

            // Waiting few seconds to allow next API call to have some time for the data to accumulate after Transaction ends
            Thread.sleep(5000);

            // Extract relevant properties from the Performance Transaction API Response
            speedIndex = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "speedIndex");
            cpuAvg = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "cpuAvg");
            cpuMax = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "cpuMax");
            memAvg = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "memAvg");
            memMax = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "memMax");
            batteryAvg = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "batteryAvg");
            batteryMax = helper.getPropertyFromPerformanceTransactionAPI(transactionId, "batteryMax");

            // Add a custom step to the Automated Test Results with a link reference to the Performance Transaction Report
            helper.addReportStep(link);
            helper.addReportStep("Total Time to Launch & Load Application in ms: " + speedIndex);
        } catch (Exception e) {
            System.out.println("Something went wrong in the script for Test: '" + method.getName() + "'");
        }

        // Add custom properties that allow for easier filtering for the Automated Test Results
        helper.addPropertyForReporting("nvProfile", nvProfile);
        helper.addPropertyForReporting("captureLevel", captureLevel);
        helper.addPropertyForReporting("speedIndex", speedIndex);
        helper.addPropertyForReporting("cpuAvg", cpuAvg);
        helper.addPropertyForReporting("cpuMax", cpuMax);
        helper.addPropertyForReporting("memAvg", memAvg);
        helper.addPropertyForReporting("memMax", memMax);
        helper.addPropertyForReporting("batteryAvg", batteryAvg);
        helper.addPropertyForReporting("batteryMax", batteryMax);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        driver.quit();
    }

}
