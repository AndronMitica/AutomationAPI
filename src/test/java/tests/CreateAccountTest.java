package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import objectData.request.RequestAccount;
import objectData.response.ResponseAccountSucces;
import objectData.response.ResponseTokkenSuccess;
import org.testng.Assert;
import org.testng.annotations.Test;
import propertiesUtility.PropertiesUtility;

public class CreateAccountTest {

    public RequestAccount requestAccountBody;
    public String token;
    public String userID;

    @Test
    public void testMethod() {
        System.out.println("=== STEP 1: CREATE NEW ACCOUNT");
        createAccount();
        System.out.println("=== STEP 2: GENERATE TOKEN ");
        generateToken();
        System.out.println("=== STEP 3: AUTHORIZATION ");
        checkAccPresence();
        System.out.println("=== STEP 4: DELETE ");
        deleteUser();
        System.out.println("=== STEP 5: RECHECK ");
        checkAccPresence();
    }

    public void createAccount() {
        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        //pregatim request-ul
        PropertiesUtility propertiesUtility = new PropertiesUtility("request/CreateAccountData");
        requestAccountBody = new RequestAccount(propertiesUtility.getAllData());

        //executam request-ul
        requestSpecification.body(requestAccountBody);
        Response response = requestSpecification.post("Account/v1/User");

        //validam response-ul
        System.out.println(response.getStatusLine());
        Assert.assertTrue(response.getStatusLine().contains("201"));
        Assert.assertTrue(response.getStatusLine().contains("Created"));

        ResponseAccountSucces responseBody = response.body().as(ResponseAccountSucces.class);
        //responseBody.prettyPrint();
        userID = responseBody.getUserId();
        Assert.assertEquals(requestAccountBody.getUserName(), responseBody.getUsername());
//        System.out.println(responseBody.getUserId());
    }

    public void generateToken() {
        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        //executam request-ul
        requestSpecification.body(requestAccountBody);
        Response response = requestSpecification.post("Account/v1/GenerateToken");

        //validam response-ul
        System.out.println(response.getStatusLine());

        Assert.assertTrue(response.getStatusLine().contains("200"));
        Assert.assertTrue(response.getStatusLine().contains("OK"));

        ResponseTokkenSuccess responseTokkenSuccess = response.body().as(ResponseTokkenSuccess.class);
        token = responseTokkenSuccess.getToken();
        Assert.assertEquals(responseTokkenSuccess.getStatus(), "Success");
        Assert.assertEquals(responseTokkenSuccess.getResult(), "User authorized successfully.");
    }

    public void checkAccPresence() {
        //configuram clientul
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        // ne autorizam pe baza la token
        requestSpecification.header("Authorization", "Bearer " + token);

        //executam request-ul
        Response response = requestSpecification.get("Account/v1/User/" + userID);

        //validam response-ul
        System.out.println(response.getStatusLine());

        if(response.getStatusLine().contains("200")){
            Assert.assertTrue(response.getStatusLine().contains("200"));
            Assert.assertTrue(response.getStatusLine().contains("OK"));
        } else {
            Assert.assertTrue(response.getStatusLine().contains("401"));
            Assert.assertTrue(response.getStatusLine().contains("Unauthorized"));
        }
    }
    public void deleteUser() {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.baseUri("https://demoqa.com/");
        requestSpecification.contentType("application/json");

        // ne autorizam pe baza la token
        requestSpecification.header("Authorization", "Bearer " + token);

        //executam request-ul
        Response response = requestSpecification.get("Account/v1/User/" + userID);

        //executam request-ul
        response = requestSpecification.delete("Account/v1/User/" + userID);

        System.out.println(response.getStatusLine());
        Assert.assertTrue(response.getStatusLine().contains("204"));
        Assert.assertTrue(response.getStatusLine().contains("No Content"));
    }
}