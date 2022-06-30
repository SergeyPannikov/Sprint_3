import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateCourier {
    public int courierId;
    public CourierAccount courierAccount = new CourierAccount("kuku01","12343224","kuku01");
    private CourierAccount courierAccountTest;
    public final static String BASEURI = "https://qa-scooter.praktikum-services.ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = BASEURI;
    }

    @After
    public void deleteCourier(){
        if (given().header("Content-type", "application/json").
                and().body(courierAccount).when().post("api/v1/courier/login").then().extract().statusCode() == 200) {
            courierId = given().header("Content-type", "application/json").
                    and().body(courierAccount).when().post("api/v1/courier/login").then().statusCode(200).and().extract().body().path("id");
            given().and().when().delete("api/v1/courier/" + courierId).then().statusCode(200).and().assertThat().body("ok", equalTo(true));
        }
    }


    @DisplayName("Успешное создание курьера, проверка кода успешного ответа и тела ответа об успешном создании курьера")
    @Test
    public void testSuccessfulCreateCourier(){
        given().header("Content-type", "application/json").
                and().body(courierAccount).when().post("api/v1/courier").then().statusCode(201).and().assertThat().body("ok",equalTo(true));
    }

    @DisplayName("Проверка, что нельзя создать двух одинаковых курьеров")
    @Test
    public void testErrorWhenCreateTwoIdenticalCourier(){
        testSuccessfulCreateCourier();
        courierAccountTest = new CourierAccount("kuku01","12343224","kuku01");
        given().header("Content-type", "application/json").
                and().body(courierAccountTest).when().post("api/v1/courier").then().statusCode(409).and().assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @DisplayName("Проверка, что при создании курьера необходимо передать поле login")
    @Test
    public void testWhenNoFieldLogin(){
        courierAccountTest = new CourierAccount(null,"12343224","kuku01");
        given().header("Content-type", "application/json").
                and().body(courierAccountTest).when().post("api/v1/courier").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Проверка, что при создании курьера необходимо передать поле password")
    @Test
    public void testWhenNoFieldIsPassword(){
        courierAccountTest = new CourierAccount("kuku01",null,"kuku01");
        given().header("Content-type", "application/json").
                and().body(courierAccountTest).when().post("api/v1/courier").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"));
    }

    @DisplayName("Проверка, что при создании курьера необходимо передать поле firstName")
    @Test
    public void testWhenNoFieldFirstName(){
        courierAccountTest = new CourierAccount("kuku01","12343224",null);
        given().header("Content-type", "application/json").
                and().body(courierAccountTest).when().post("api/v1/courier").then().statusCode(201);
        //тут получается ошибка документации, так как поле firstName не указано как необязательное ,а проверка проходит только по логину и паролю, поэтому создается логин
    }

    @DisplayName("Проверка, что если попробовать создать с существующим логином , но другим паролем и именем , то будет ошибка")
    @Test
    public void testErrorWhenCreateIdenticalCourierLogin(){
        testSuccessfulCreateCourier();
        courierAccountTest = new CourierAccount("kuku01","1234322454554","kuku014545");
        given().header("Content-type", "application/json").
                and().body(courierAccountTest).when().post("api/v1/courier").then().statusCode(409).and().assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
    }

}
