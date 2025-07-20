package core;

import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;

public class Listener implements ITestListener {

    public Listener(){

    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Started Test: "+result.getMethod());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("\nTEST PASSED\n");

    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("\nTEST FAILED\n"+result.getName());

        Object resobj=result.getAttribute("apiresponse");
        if(resobj instanceof Response){
            Response response=(Response) resobj;
            saveResponseToFile(response.asPrettyString(),result.getName());
        }
    }

    private void saveResponseToFile(String response, String testName) {
        try {
            String fileName = "failure_logs/" + testName +  ".txt";

            FileWriter writer = new FileWriter(fileName);
            writer.write(response);
            writer.close();

            System.out.println(" API Response saved to: " + fileName);
        } catch (IOException e) {
            System.out.println(" Failed to save API response: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ITestListener.super.onTestSkipped(result);

        System.out.println("TEST SKIPPED" + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("===TEST SUITE STARTED===");

    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("===TEST SUITE FINISHED===");
    }


}
