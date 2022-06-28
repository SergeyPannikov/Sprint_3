import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateOrder {
    private int trackId;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @After
    public void CleanOrders(){
        given().header("Content-type", "application/json").
                queryParam("track", trackId).
                and().when().put("api/v1/orders/cancel").then().statusCode(200).and().assertThat().body("ok",equalTo(true));

    }

    @DisplayName("Проверка успешного создания заказа с двумя цветами и проверка в ответа что есть трек заказа")
    @Test
    public void testCreateOrdersWithTwoColors(){
        Order order = new Order("Test","Testovich","MOscow dept 5", 4, "+7 988 434 43 21",7,"2022-06-30","Faster!!Faster!!", new String[]{"BLACK","GREY"});
        trackId = given().header("Content-type", "application/json").
                and().body(order).when().post("api/v1/orders").then().statusCode(201).and().extract().body().path("track");
    }

    @DisplayName("Проверка успешного создания заказа с двумя цветами")
    @Test
    public void testCreateOrdersWithOneColor(){
        Order order = new Order("Test2","Testovich123","MOscow dept 511", 4, "+7 988 434 43 44",7,"2022-06-30","Faster!!Faster!!", new String[]{"BLACK"});
        trackId = given().header("Content-type", "application/json").
                and().body(order).when().post("api/v1/orders").then().statusCode(201).and().extract().body().path("track");
    }

    @DisplayName("Проверка успешного создания заказа без цветов")
    @Test
    public void testCreateOrdersWithoutColors(){
        Order order = new Order("Test3","Testovich321","MOscow dept 522", 4, "+7 988 434 43 32",7,"2022-06-29","Faster!!Faster!!");
        trackId = given().header("Content-type", "application/json").
                and().body(order).when().post("api/v1/orders").then().statusCode(201).and().extract().body().path("track");
    }
}
