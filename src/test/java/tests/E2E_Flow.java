package tests;

import com.experitest.reporter.testng.Listener;
import helpers.Helpers;
import helpers.PropertiesReader;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

@Listeners(Listener.class)
public class E2E_Flow {

    /**
     *
     * ==================================================
     *                      READ ME                     =
     * ==================================================
     *
     * In the SeeTestCloud, we have the ability to capture Performance Metrics for our Mobile Tests.
     * In the Report, we capture data such as Average & Maximum consumed CPU / Memory / Battery, as well as
     * Network Traffic, Speed Index, and if applicable, download / upload speed for the Network Profile applied.
     *
     * https://docs.experitest.com/display/TE/StartPerformanceTransactionForApplication
     * https://docs.experitest.com/display/TE/EndPerformanceTransaction
     *
     * https://docs.experitest.com/display/TE/Transaction+report
     * https://docs.experitest.com/display/TE/Transaction+View
     *
     */

    protected IOSDriver<IOSElement> driver = null;
    protected DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    protected WebDriverWait wait;
    protected Helpers helper;

    @BeforeClass
    public void setUp(ITestContext context) throws MalformedURLException {
        String deviceQuery =  context.getCurrentXmlTest().getParameter("deviceQuery");

        desiredCapabilities.setCapability("testName", "EriBank_E2E_Payment_Flow");
        desiredCapabilities.setCapability("accessKey", new PropertiesReader().getProperty("accessKey"));
        desiredCapabilities.setCapability("deviceQuery", deviceQuery);
        desiredCapabilities.setCapability("autoAcceptAlerts", true);

        driver = new IOSDriver<>(new URL(new PropertiesReader().getProperty("cloudUrl")), desiredCapabilities);
        wait = new WebDriverWait(driver, 10);
        helper = new Helpers(driver);
    }

    @Test(priority = 1)
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

            // Extract relevant properties from the Transaction Response
            String link = helper.getPropertyFromPerformanceTransactionReport(response, "link");

            // Add a custom step to the Automated Test Results with a link reference to the Performance Transaction Report
            helper.addReportStep(link);
        } catch (Exception e) {
            System.out.println("Something went wrong in the script for Test: '" + method.getName() + "'");
            e.printStackTrace();
        }

        // Add custom properties that allow for easier filtering for the Automated Test Results
        helper.addPropertyForReporting("nvProfile", nvProfile);
        helper.addPropertyForReporting("captureLevel", captureLevel);
    }

    @Test(priority = 2, dependsOnMethods = {"test_launch_of_application_response"})
    @Parameters({"nvProfile", "captureLevel"})
    public void test_login_response_time(String nvProfile, String captureLevel, @Optional Method method) {
        try {
            helper.startGroupingOfSteps(method.getName());

            driver.findElement(By.name("usernameTextField")).sendKeys("company");
            driver.findElement(By.name("passwordTextField")).sendKeys("company");

            helper.startCapturePerformanceMetrics(nvProfile, captureLevel, "com.experitest.ExperiBank");

            driver.findElement(By.name("loginButton")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.name("Make Payment")));

            String response = helper.endCapturePerformanceMetrics(method.getName());

            helper.endGroupingOfSteps();

            String link = helper.getPropertyFromPerformanceTransactionReport(response, "link");
            helper.addReportStep(link);
        } catch (Exception e) {
            System.out.println("Something went wrong in the script for Test: '" + method.getName() + "'");
            e.printStackTrace();
        }

        helper.addPropertyForReporting("nvProfile", nvProfile);
        helper.addPropertyForReporting("captureLevel", captureLevel);
    }

    @Test(priority = 3, dependsOnMethods = {"test_login_response_time"})
    @Parameters({"nvProfile", "captureLevel"})
    public void test_payment_response_time(String nvProfile, String captureLevel, @Optional Method method) {
        try {
            helper.startGroupingOfSteps(method.getName());

            driver.findElement(By.name("Make Payment")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.name("phoneTextField")));

            driver.findElement(By.name("phoneTextField")).sendKeys("3479350000");
            driver.findElement(By.name("nameTextField")).sendKeys("Rahee");
            driver.findElement(By.name("amountTextField")).sendKeys("20");
            driver.findElement(By.name("countryButton")).click();

            wait.until(ExpectedConditions.elementToBeClickable(By.name("Switzerland")));
            driver.findElement(By.name("Switzerland")).click();

            helper.startCapturePerformanceMetrics(nvProfile, captureLevel, "com.experitest.ExperiBank");

            driver.findElement(By.name("sendPaymentButton")).click();
            driver.findElement(By.name("Yes")).click();

            wait.until(ExpectedConditions.elementToBeClickable(By.id("Make Payment")));

            String response = helper.endCapturePerformanceMetrics(method.getName());

            helper.endGroupingOfSteps();

            String link = helper.getPropertyFromPerformanceTransactionReport(response, "link");
            helper.addReportStep(link);
        } catch (Exception e) {
            System.out.println("Something went wrong in the script for Test: '" + method.getName() + "'");
            e.printStackTrace();
        }

        helper.addPropertyForReporting("nvProfile", nvProfile);
        helper.addPropertyForReporting("captureLevel", captureLevel);
    }

    @Test(priority = 4, dependsOnMethods = {"test_payment_response_time"})
    @Parameters({"nvProfile", "captureLevel"})
    public void test_logout_response_time(String nvProfile, String captureLevel, @Optional Method method) {
        try {
            helper.startGroupingOfSteps(method.getName());

            helper.startCapturePerformanceMetrics(nvProfile, captureLevel, "com.experitest.ExperiBank");

            driver.findElement(By.name("Logout")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.name("usernameTextField")));

            String response = helper.endCapturePerformanceMetrics(method.getName());

            helper.endGroupingOfSteps();

            String link = helper.getPropertyFromPerformanceTransactionReport(response, "link");
            helper.addReportStep(link);
        } catch (Exception e) {
            System.out.println("Something went wrong in the script for Test: '" + method.getName() + "'");
            e.printStackTrace();
        }

        helper.addPropertyForReporting("nvProfile", nvProfile);
        helper.addPropertyForReporting("captureLevel", captureLevel);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown(ITestContext context) {
        try {
            String qaReleaseCycle = context.getCurrentXmlTest().getParameter("QA_Release_Cycle");
            driver.executeScript("seetest:client.addTestProperty(\"QA_Release_Cycle\", \"" + qaReleaseCycle + "\")");
        } catch (Exception e) {
            System.out.println("WARNING - Property or value not found, ignoring adding of Test Property using 'client.addTestProperty'");
            e.printStackTrace();
        }

        driver.quit();
    }

}