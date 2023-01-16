package helpers;

import io.appium.java_client.AppiumDriver;
import org.json.JSONObject;

public class Helpers {

    protected AppiumDriver driver;

    public Helpers(AppiumDriver driver) {
        this.driver = driver;
    }

    public void startCapturePerformanceMetrics(String nvProfile, String captureLevel) {
        try {
            if (captureLevel.equalsIgnoreCase("Device")) {
                driver.executeScript("seetest:client.startPerformanceTransaction(\"" + nvProfile + "\")");
            } else if (captureLevel.equalsIgnoreCase("Application")) {
                driver.executeScript("seetest:client.startPerformanceTransactionForApplication(\"com.experitest.ExperiBank\", \"" + nvProfile + "\")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not start Capturing. Accepted Values: [Device, Application]");
        }
    }

    public String endCapturePerformanceMetrics(String transactionName) {
        Object transaction = driver.executeScript("seetest:client.endPerformanceTransaction(\"" + transactionName + "\")");
        return transaction.toString();
    }

    // Properties that can be fetched:
    // transactionName / transactionId / appName / appVersion / link (Link to Performance Transaction Report)
    public String getPropertyFromPerformanceTransactionReport(String response, String property) {
        JSONObject jsonObject = new JSONObject(response);
        String text = jsonObject.getString("text");
        JSONObject textObject = new JSONObject(text);
        property = textObject.get(property).toString();
        return property;
    }

    public void setReportStatus(String status, String message) {
        driver.executeScript("seetest:client.setReportStatus(\"" + status + "\", \"" + status + "\", \"" + message + "\")");
    }

    public void addReportStep(String input) {
        driver.executeScript("seetest:client.report(\"" + input + "\", \"true\")");
    }

    public void addReportStep(String input, String status) {
        driver.executeScript("seetest:client.report(\"" + input + "\", \"" + status + "\")");
    }

    public void addPropertyForReporting(String property, String value) {
        driver.executeScript("seetest:client.addTestProperty(\"" + property + "\", \"" + value + "\")");
    }

    public void startGroupingOfSteps(String testName) {
        driver.executeScript("seetest:client.startStepsGroup(\"" + testName + "\")");
    }

    public void endGroupingOfSteps() {
        driver.executeScript("seetest:client.stopStepsGroup()");
    }

}
