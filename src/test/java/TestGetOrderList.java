import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class TestGetOrderList {

    @Before
    public void setUp() {
        RestAssured.baseURI = TestCreateCourier.BASEURI;
    }

    @DisplayName("Проверка что тело ответа возвращает непустой список заказов")
    @Test
    public void testOrdersList(){
        given().and().when().get("api/v1/orders").then().statusCode(200).and().assertThat().body("orders", notNullValue());
    }
}
