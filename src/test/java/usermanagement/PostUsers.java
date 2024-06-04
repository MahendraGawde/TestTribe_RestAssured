package usermanagement;


import core.StatusCode;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import pojo.postReqExample;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class PostUsers {
    @Test
    public void validatePostWithPojo(){
        postReqExample postReq = new postReqExample();
        postReq.setName("Marvel");
        postReq.setJob("Avengers");
        Response response = given()
                .header("Content-Type", "application/json")
                .body(postReq)
                .when()
                .post("https://reqres.in/api/users");
        assertEquals(response.getStatusCode(), StatusCode.CREATED.code);
        System.out.println("validateWithPojo executed successfully");
        System.out.println(response.getBody().asString());
    }
}
