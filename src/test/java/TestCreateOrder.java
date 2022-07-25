import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class TestCreateOrder {

    private String firstName;
    private String lastName;
    private String address;
    private int metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    public TestCreateOrder(String firstName, String lastName, String address, int metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrderFields() {
        return new Object[][] {
                {"Test","Testovich","MOscow dept 5", 4, "+7 988 434 43 21",7,"2022-06-30","Faster!!Faster!!", new String[]{"BLACK","GREY"}},
                {"Test2","Testovich123","MOscow dept 511", 4, "+7 988 434 43 44",7,"2022-06-30","Faster!!Faster!!", new String[]{"BLACK"}},
                {"Test3","Testovich321","MOscow dept 522", 4, "+7 988 434 43 32",7,"2022-06-29","Faster!!Faster!!",null},
        };
    }


    private int trackId;

    @Before
    public void setUp() {
        RestAssured.baseURI = TestCreateCourier.BASEURI;
    }

    @After
    public void cleanOrders(){
        given().header("Content-type", "application/json").
                queryParam("track", trackId).
                and().when().put("api/v1/orders/cancel").then().statusCode(200).and().assertThat().body("ok",equalTo(true));

    }

    @DisplayName("Проверка успешного создания заказа c разными параметрами")
    @Test
    public void testCreateOrdersWithParameters(){
        Order order = new Order( firstName,  lastName,  address,  metroStation,  phone,  rentTime,  deliveryDate,  comment, color);
        trackId = given().header("Content-type", "application/json").
                and().body(order).when().post("api/v1/orders").then().statusCode(201).and().extract().body().path("track");
        System.out.println(trackId);
    }

}
