package com.bank;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BankATMAutomation {

    private String baseURL = "https://examplebankapi.com";
    private String accessToken;

    @BeforeClass
    public void setUp() {

        accessToken = getToken("username", "password");
    }

    @Test
    public void testWithdrawAmount() {

        int amountToWithdraw = 100;
        Response response = withdrawAmount(amountToWithdraw);

        Assert.assertEquals(response.getStatusCode(), 200);

        String transactionId = response.jsonPath().getString("transactionId");
        int remainingBalance = response.jsonPath().getInt("remainingBalance");

        Assert.assertNotNull(transactionId);
        Assert.assertTrue(remainingBalance < 1000);
    }

    private String getToken(String username, String password) {

        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}")
                .post(baseURL + "/auth/token");

        return response.jsonPath().getString("accessToken");
    }

    private Response withdrawAmount(int amount) {

        String requestBody = String.format("{\"amount\": %d}", amount);
        return RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(requestBody)
                .post(baseURL + "/withdraw");
    }
}
