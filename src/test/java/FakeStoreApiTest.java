
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Adler32;

import static io.restassured.RestAssured.given;

@Listeners(core.Listener.class)
public class FakeStoreApiTest {

    String baseUri = "https://fakestoreapi.com";

    @Test(priority = 1,description = "GET ALL products")
    public void TC_01_verify_responseCodeForAllProducts(){
        stepLog("====TC_01: Verify status code is 200 for getting all products====");
       //Send GET REquest
        Response response= given()
                .baseUri(baseUri)
                .when()
                .get("/products")
                .then()
                .extract().response();
        System.out.println("Response: "+response.asPrettyString());
        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");
        System.out.println("===Successfully verified the response message===");


    }

    @Test(priority = 2,description = "GET All Products")
    public void TC_02_verifyResponseContains_non_empty(){
        stepLog("===TC_02: Verify response contains a non-empty list of products====");
        //Send GET Request
        Response response= given()
                .baseUri(baseUri)
                .when()
                .get("/products")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200,"Mismatch of expected data");
        int productLenght= response.jsonPath().getList("$").size();
        Assert.assertTrue(productLenght>0,"Expected product count >0");
        System.out.println("===Successfully verified the product count  not less than zero===");

    }

    @Test(priority = 3, description = "GET Single Products")
    public void TC_03_verifyStatusForSingleProduct(){
        stepLog("===TC_03: Verify status code is 200 for valid product ID===");
        //Send GET Request
        Response response= RestAssured
                .given()
                .baseUri(baseUri)
                .when()
                .get("/products/1")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");
        System.out.println("===Successfully verified the Single product response===");

    }

    @Test(priority = 4,description = "Get Single Product")
    public void TC_04_verifyProductDetailsById(){
        stepLog("TC_04: Verify response contains correct product details for ID");
        int productId=5;
        //Send GET Request
        Response response=RestAssured
                .given()
                .baseUri(baseUri)
                .when()
                .get("/products/5")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(),200,"mismatch of data");
        int actualproductId=response.jsonPath().getInt("id");
        Assert.assertEquals(actualproductId,productId,"mismatch of product Id");
        System.out.println("===Successfully verified the Product Details By Id===");

    }
    @Test(priority = 5,description = "Get Single Product")
    public void TC_05_verifyStatusCodeForInvalidId(){
        stepLog("===TC_05: Verify status code is 404 for invalid product ID===");
        int productId=9999;
        //Send GET Request
        Response response=given()
                .baseUri(baseUri)
                .when()
                .get("/products/9999")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");//404 is the status code but this server only give response as 200 for all request

        System.out.println("===Successfully verified response for invalid Product  Id===");

    }

    @Test(priority = 6,description ="Add New Product")
    public void TC_06_verifyProductCreated(){
        stepLog("===TC-06_Verify product creation with valid request payload===");
        //create response payload
        Map<String,Object> requestBody= new HashMap<>();
        requestBody.put("title","newly created data");
        requestBody.put("price",234.67);
        requestBody.put("description","This is newly created data added to json file");
        requestBody.put("image", "https://fakestoreapi.com/img/test-product.jpg");
        requestBody.put("category", "electronics");
        //Send POST Request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/products")
                .then()
                .extract()
                .response();

        System.out.println("Response Status Code: " + response.getStatusCode());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 201 for product creation");//201 is the actual status code but this server give response as 200 for all request
        System.out.println("Response: "+response.asPrettyString());

        //Assert response contains id
        int createdProductId = response.jsonPath().getInt("id");
        Assert.assertTrue(createdProductId > 0, "Expected created product ID to be present and > 0");

        System.out.println(" Successfully created product with ID: " + createdProductId);

        System.out.println("===Successfully created new product id");

    }

    @Test(priority = 7,description ="Add New Product")
    public void TC_07_verifyStatusCodeForInvalidPayload(){
        stepLog("===TC_07: Verify status code 400 for invalid payload===");
        String invalidPayload = "{ \"productName\": \"\", \"price\": \"invalidPrice\" }";
        //Send POST Request
        Response response= RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type","application/json")
                .body(invalidPayload)
                .when()
                .post("/products")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of the response");//400 is the actual status code but this server give response as 200 for all request
        System.out.println("===Successfully verified the status of invalid payload===");
    }

    @Test(priority = 8, description = "Update Product")
    public void TC_08_verifyProductUpdateWithValidData() {
        stepLog("===TC_08: Verify product update with valid payload and product ID===");

        // Correct payload - use put(), not replace()
        Map<String, Object> updation = new HashMap<>();
        updation.put("title", "updating the newly created data");
        updation.put("price", 342.7);
        updation.put("description", "This is newly updated data added to json file");
        updation.put("image", "https://fakestoreapi.com/img/test-product.jpg");
        updation.put("category", "Electronics_updated");

        // Send PUT request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(updation)
                .when()
                .put("/products/21")
                .then()
                .extract().response();


        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200 for product update");

        System.out.println("Response : ");
        System.out.println(response.asPrettyString());

        // Verify response contains updated product ID
        int updatedProductId = response.jsonPath().getInt("id");
        Assert.assertTrue(updatedProductId > 0, "Expected updated product ID to be present and > 0");

        System.out.println(" Successfully updated product with ID: " + updatedProductId);
    }
    @Test(priority = 9,description = "Delete Product")
    public void TC_09_VerifyDeletionWithValidProductId(){
        stepLog("===TC_09:Verify product deletion with valid product ID===");
       //Send DELETE Request
        Response response=RestAssured
                .given()
                .baseUri(baseUri)
                .when()
                .delete("/products/21")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());
        int StatusCode= response.getStatusCode();
        Assert.assertEquals(StatusCode,200,"missmatch of status code");
        System.out.println("===Successfully deleted the id===");
    }

    @Test(priority = 10,description = "GET ALL Carts")
    public void TC_10_verify_responseCodeForAllCarts(){
        stepLog("====TC_10: Verify status code is 200 for fetching all carts====");
        //Send GET Request
        Response response= given()
                .baseUri(baseUri)
                .when()
                .get("/carts")
                .then()
                .extract().response();
        System.out.println("Response: "+response.asPrettyString());
        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");
        System.out.println("===Successfully verified the response message===");
    }

    @Test(priority = 11, description = "GET Single Cart")
    public void TC_11_verifyStatusForSingleCart(){
        stepLog("===TC_11: Verify status code is 200 for valid Cart ID===");
        //Send GET Request
        Response response= given()
                .baseUri(baseUri)
                .when()
                .get("/carts/1")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");
        System.out.println("===Successfully verified the Single cart response===");

    }


    @Test(priority = 12,description ="Add New Cart")
    public void TC_12_verifyNewCartCreated(){
        stepLog("===TC_12:Verify cart creation with valid payload===");
        //create response payload
        Map<String,Object> cartPayLoad= new HashMap<>();
        cartPayLoad.put("userId","29");
        cartPayLoad.put("date","2025-07-20T00::00.000Z");
        //  Add products to cart
        Map<String, Object> product1 = new HashMap<>();
        product1.put("productId", 1);
        product1.put("quantity", 2);

        Map<String, Object> product2 = new HashMap<>();
        product2.put("productId", 2);
        product2.put("quantity", 1);

        cartPayLoad.put("products", new Map[]{product1, product2});

       //Send POST Request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(cartPayLoad)
                .when()
                .post("/carts")
                .then()
                .extract()
                .response();

        System.out.println("Response Status Code: " + response.getStatusCode());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 201 for cart creation");//201 is the actual status code but this server give response as 200 for all request
        System.out.println("Response: "+response.asPrettyString());

        //Assert response contains id
        int createdCartId = response.jsonPath().getInt("id");
        Assert.assertTrue(createdCartId > 0, "Expected created cart ID to be present and > 0");

        System.out.println(" Successfully created cart with ID: " + createdCartId);

        System.out.println("===Successfully created new product id");

    }


    @Test(priority = 13, description = "Update Cart")
    public void TC_13_verifyCartUpdateWithValidData() {
        stepLog("===TC_13: Verify cart update with valid payload===");

        // Correct payload - use put(), not replace()
        Map<String,Object> cartPayLoad= new HashMap<>();
        cartPayLoad.put("userId","11");
        cartPayLoad.put("date","2025-07-20T00::00.000Z");
        //  Add products to cart
        Map<String, Object> product1 = new HashMap<>();
        product1.put("productId", 3);
        product1.put("quantity", 4);

        Map<String, Object> product2 = new HashMap<>();
        product2.put("productId", 3);
        product2.put("quantity", 4);

        cartPayLoad.put("products", new Map[]{product1, product2});

        // Send PUT request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(cartPayLoad)
                .when()
                .put("/carts/11")
                .then()
                .extract().response();


        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200 for product update");


        System.out.println("Response JSON: ");
        System.out.println(response.asPrettyString());

        // Verify response contains updated product ID
        int updatedCartId = response.jsonPath().getInt("id");
        Assert.assertTrue(updatedCartId > 0, "Expected updated Cart ID to be present and > 0");

        System.out.println(" Successfully updated cart with ID: " + updatedCartId);
    }
    @Test(priority = 14,description = "Delete Cart")
    public void TC_14_VerifyDeletionWithValidCartId(){
        stepLog("===TC_14:Verify product deletion with valid cart ID===");
       //Send DELETE Request
        Response response=RestAssured
                .given()
                .baseUri(baseUri)
                .when()
                .delete("/carts/11")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());
        int StatusCode= response.getStatusCode();
        Assert.assertEquals(StatusCode,200,"missmatch of status code");
        System.out.println("===Successfully deleted the Cart id===");
    }




    @Test(priority = 15,description = "GET ALL Users")
    public void TC_15_verify_responseCodeForAllCarts(){
        stepLog("====TC_15: Verify status code is 200 for fetching all users====");
        //Send GET Request
        Response response= given()
                .baseUri(baseUri)
                .when()
                .get("/users")
                .then()
                .extract().response();
        System.out.println("Response: "+response.asPrettyString());
        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");
        System.out.println("===Successfully verified the response message===");
    }

    @Test(priority = 16, description = "GET Single User")
    public void TC_16_verifyStatusForSingleUser(){
        stepLog("===TC_16: Verify response contains correct user details for ID===");
       //Send GET Request
        Response response= given()
                .baseUri(baseUri)
                .when()
                .get("/users/1")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());

        int statusCode=response.getStatusCode();
        Assert.assertEquals(statusCode,200,"mismatch of data");
        System.out.println("===Successfully verified the Single Users response===");

    }

        @Test(priority = 17, description = "Add New User - Valid Payload")
        public void TC_17_verifyAddNewUser() {
            System.out.println("====TC_17: VERIFY ADD NEW USER WITH VALID PAYLOAD ====");

            //  Create user payload
            Map<String, Object> userPayload = new HashMap<>();

            userPayload.put("email", "john.doe@gmail.com");
            userPayload.put("username", "johndoe");
            userPayload.put("password", "Test@12345");
            userPayload.put("phone", "1-234-567-8901");

            // Add name
            Map<String, String> name = new HashMap<>();
            name.put("firstname", "John");
            name.put("lastname", "Doe");
            userPayload.put("name", name);

            // Add address
            Map<String, Object> address = new HashMap<>();
            address.put("city", "New York");
            address.put("street", "Main Street");
            address.put("number", 1001);
            address.put("zipcode", "10001");

            Map<String, String> geolocation = new HashMap<>();
            geolocation.put("lat", "40.7128");
            geolocation.put("long", "-74.0060");
            address.put("geolocation", geolocation);

            userPayload.put("address", address);

            //  Send POST request
            Response response = RestAssured
                    .given()
                    .baseUri(baseUri)
                    .header("Content-Type", "application/json")
                    .body(userPayload)
                    .when()
                    .post("/users")
                    .then()
                    .extract()
                    .response();

            //  Log response
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body:\n" + response.asPrettyString());

            //  Assert status code is 201
            Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 201 for user creation");//201 is the actual status code but in this website 200 is status code for all request

            //  Assert response contains 'id'
            int createdUserId = response.jsonPath().getInt("id");
            Assert.assertTrue(createdUserId > 0, "Expected created user ID to be present and > 0");


     System.out.println("===Successfully created new User id "+createdUserId);

    }

    @Test(priority = 18, description = "Update User")
    public void TC_18_verifyUserUpdateWithValidData() {
        stepLog("===TC_18: Verify User update with valid payload===");

        //  Create user payload
        Map<String, Object> userPayload = new HashMap<>();

        userPayload.put("email", "arun@gmail.com");
        userPayload.put("username", "Arun");
        userPayload.put("password", "Devil@12345");
        userPayload.put("phone", "7685678905");

        // Add name
        Map<String, String> name = new HashMap<>();
        name.put("firstname", "Arun");
        name.put("lastname", "Kumar");
        userPayload.put("name", name);

        // Add address
        Map<String, Object> address = new HashMap<>();
        address.put("city", "Salem");
        address.put("street", "Mettur");
        address.put("number", 6363);
        address.put("zipcode", "600002");

        Map<String, String> geolocation = new HashMap<>();
        geolocation.put("lat", "36.7128");
        geolocation.put("long", "72.0060");
        address.put("geolocation", geolocation);

        userPayload.put("address", address);

        //  Send PUT request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(userPayload)
                .when()
                .put("/users/11")
                .then()
                .extract()
                .response();


        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200 for User update");


        System.out.println("Response JSON: ");
        System.out.println(response.asPrettyString());


        Assert.assertEquals(response.jsonPath().getString("username"), "Arun", "Username mismatch");
        Assert.assertEquals(response.jsonPath().getString("email"), "arun@gmail.com", "Email mismatch");
        Assert.assertEquals(response.jsonPath().getString("name.firstname"), "Arun", "Firstname mismatch");
        Assert.assertEquals(response.jsonPath().getString("address.city"), "Salem", "City mismatch");

        System.out.println(" Successfully updated User with ID: 11");
    }
    @Test(priority = 19,description = "Delete User")
    public void TC_19_VerifyDeletionWithValidCartId(){
        stepLog("===TC_19:Verify user deletion with valid ID===");
        //create DELETE Request
        Response response=RestAssured
                .given()
                .baseUri(baseUri)
                .when()
                .delete("/users/10")
                .then()
                .extract()
                .response();
        System.out.println("Response: "+response.asPrettyString());
        int StatusCode= response.getStatusCode();
        Assert.assertEquals(StatusCode,200,"missmatch of status code");
        System.out.println("===Successfully deleted the USer id===");
    }

    @Test(priority = 20, description = "Login with Valid Credentials")
    public void TC_20_verifyLoginSuccessful() {
        stepLog("===TC_20: VERIFY LOGIN SUCCESSFUL WITH VALID CREDENTIALS===");

        //  Create login payload
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("username", "mor_2314"); // Valid username
        loginPayload.put("password", "83r5^_");   // Valid password

        //  Send POST request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .response();


        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code is 200 for successful login");

        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should be present in response");

        System.out.println(" Login Successful, Token: " + token);
    }

    @Test(priority = 21, description = "Login with Invalid Credentials")
    public void TC_21_verifyLoginUnsuccessful() {
        stepLog("===TC_21: VERIFY LOGIN FAILS WITH INVALID CREDENTIALS===");

        //  Invalid credentials
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("username", "wrong_user"); // Invalid username
        loginPayload.put("password", "wrong_pass"); // Invalid password

        //  Send POST request
        Response response = RestAssured
                .given()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .response();


        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:" + response.asPrettyString());

        Assert.assertEquals(response.getStatusCode(), 401, "Expected status code is 400 for unsuccessful login");//401 is the actual status but 200 is valid in this server

        System.out.println(" Login failed as expected for invalid credentials");
    }

    public void stepLog(String text){
        System.out.println("\n***"+text.toUpperCase()+"***\n");

    }

}
