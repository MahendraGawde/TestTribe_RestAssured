package usermanagement;

import core.StatusCode;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.parser.ParseException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.JsonReader;
import utils.PropertyReader;
import utils.SoftAssertioniUtil;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.testng.Assert.assertEquals;

public class GetUsers {

    SoftAssertioniUtil softAssertion = new SoftAssertioniUtil();

    @Test
    public void getUserData(){
        // first test using rest assured bdd style
                given().
                when().
                get("https://reqres.in/api/users?page=2").
                then().
                assertThat().
                statusCode(200);
    }
    @Test
    public void validateGetResponseBody(){
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        //Send GET req and validate response body using 'then'
        given()
                .when()
                .get("/todos/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body(not(isEmptyString()))
                .body("title", equalTo("delectus aut autem"))
                .body("userId", equalTo(1));
    }

    @Test
    public void validateResponseHasItems(){
        RestAssured.baseURI ="https://jsonplaceholder.typicode.com";

        // Send GET request and store response in variable
        Response response = given()
                .when()
                .get("/posts")
                .then()
                .extract()
                .response();

        // Use Hamcrest to check that the response body contains specific items
        assertThat(response.jsonPath().getList("title"), hasItems("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "qui est esse"));
    }

    @Test
    public void validateResponseHasSize(){
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

        Response response = given()
                .when()
                .get("/comments")
                .then()
                .extract()
                .response();

        //Use Hamcrest to check that response body has a specific size
        assertThat(response.jsonPath().getList(""), hasSize(500));
    }

    @Test
    public void verifyStatusCodeDelete(){
        Response resp = given()
                .delete("https://reqres.in/api/users/2");
        assertEquals(resp.getStatusCode(), StatusCode.NO_CONTENT.code);
        System.out.println("verifyStatusCodeDelete executed successfully");
    }

    @Test
    public void validateWithTestDataFromJson() throws IOException, ParseException {
        String username = JsonReader.getTestData("username");
        String password = JsonReader.getTestData("password");
        System.out.println("username from Json is: "+username+ "***password from Json is: "+password);
        Response resp = given()
                .auth()
                .basic(username,password)
                .when()
                .get("https://postman-echo.com/basic-auth");

        int actualStatusCode = resp.statusCode();
        assertEquals(actualStatusCode, StatusCode.SUCCESS.code);
        System.out.println("validateWithTestDataFromJson executed successfully");
    }

    @Test
    public void validateDataFromPropertiesFile(){
        String serverAddress = PropertyReader.propertyReader("config.properties","server");
        System.out.println("Server address is: " +serverAddress);
        System.out.println("*********************");
        Response resp =
                given()
                        .queryParam("page",2)
                        .when()
                        .get(serverAddress);
        int actualStatusCode = resp.statusCode(); // RestAssured
        assertEquals(actualStatusCode,200); // TestNG
        System.out.println("validateDataFromPropertiesFile executed successfully" + serverAddress);
    }
//    @Test
//    public static JSONObject getPaypalUserData() {
//        // first test using rest assured bdd style
//        given().
//
//                when().
//                auth().basic("username", "test").
//                get("https://www.google.com").
////                get("https://api-m.sandbox.paypal.com/v1/identity/oauth2/userinfo?schema=paypalv1.1").
//        then().
//                extract().response();
//    }

    @Test
    public void validateFromProperties_TestdataFile() throws IOException, ParseException {
        String serverAddress = PropertyReader.propertyReader("config.properties","server");
        String endpoint = JsonReader.getTestData("endpoint");
//        String URL = serverAddress + endpoint;
//        System.out.println("URL address is: " +URL);
        Response resp =
                given()
                        .queryParam("page",2)
                        .when()
                        .get(serverAddress + endpoint);

        int actualStatusCode = resp.statusCode();
        assertEquals(actualStatusCode,200);
        System.out.println("validateFromProperties_TestdataFile executed successfully " + serverAddress + endpoint);
    }

    @Test
    public void softAssertion(){

        System.out.println("softAssert");
        softAssertion.assertTrue(true,"test soft assertion");
        softAssertion.assertAll();
    }
    @Test
    public void validateWithSoftAssertUtil(){
        RestAssured.baseURI="https://reqres.in/api";
        Response response = given()
                .queryParam("page",2)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .extract().response();

        response.then().body("data",hasSize(6));
        softAssertion.assertEquals(response.getStatusCode(),StatusCode.SUCCESS.code,"Status code is not 200");
        softAssertion.assertAll();
        //Assert that first user in the list has correct values
        response.then().body("data[1].id", is(8));
        response.then().body("data[1].email", is("lindsay.ferguson@reqres.in"));
        response.then().body("data[1].firstname", is("Lindsay"));
        response.then().body("data[1].lastname", is("Ferguson"));


        softAssertion.assertEquals(response.getStatusCode(), StatusCode.SUCCESS.code, "Status code is not 200");
    }

    @DataProvider(name ="testdata")
    public Object[][] testdata(){
        String Payload = "{\"name\":\"Eddie\"}";
        return new Object[][]{
                {"1","John"},
                {"2", "Tom"},
                {"3", "Bob"}
        };
    }
    @Test(dataProvider = "testdata")
    @Parameters({"id", "name"})
    public void testEndpoint(String id, String name){
        given()
                .queryParam("id", id)
                .queryParam("name", name)
                .when()
                .get("https://reqres.in/api/users")
                .then()
                .statusCode(200);

    }

}


