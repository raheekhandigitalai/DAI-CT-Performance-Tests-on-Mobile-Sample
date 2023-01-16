package tests;

import helpers.Helpers;
import helpers.PropertiesReader;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class ExceptionHandling {

    protected IOSDriver<IOSElement> driver = null;
    protected DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    protected WebDriverWait wait;
    protected Helpers helper;

    @BeforeMethod
    public void setUp(ITestContext context, Method method) throws MalformedURLException {
        String deviceQuery =  context.getCurrentXmlTest().getParameter("deviceQuery");

        desiredCapabilities.setCapability("testName", method.getName());
        desiredCapabilities.setCapability("accessKey", new PropertiesReader().getProperty("accessKey"));
        desiredCapabilities.setCapability("deviceQuery", deviceQuery);
        desiredCapabilities.setCapability("app", "cloud:com.experitest.ExperiBank");
        desiredCapabilities.setCapability("bundleId", "com.experitest.ExperiBank");
        desiredCapabilities.setCapability("autoAcceptAlerts", true);

        driver = new IOSDriver<>(new URL(new PropertiesReader().getProperty("cloudUrl")), desiredCapabilities);
        wait = new WebDriverWait(driver, 5);
        helper = new Helpers(driver);
    }

    @Test
    public void example_on_handling_exceptions_for_accurate_reporting() {
        wait.until(ExpectedConditions.elementToBeClickable(By.name("usernameTextField")));
        driver.findElement(By.name("usernameTextField")).sendKeys("username");
        driver.findElement(By.name("invalidPasswordField")).sendKeys("password"); // Will fail
        driver.findElement(By.name("loginButton")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.name("Make Payment")));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        try {
            if (result.isSuccess()) {
                driver.executeScript("seetest:client.setReportStatus(\"Passed\",\"Test Passed\")");
            } else {
                String[] cause = result.getThrowable().getMessage().split("\\R");
                helper.addPropertyForReporting("failure_cause", cause[0]);
                helper.setReportStatus("Failed", result.getThrowable().getMessage());
            }

            driver.quit();
        } catch (Exception e) {
            driver.quit();
        }

    }

}
