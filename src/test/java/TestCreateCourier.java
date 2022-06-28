import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCreateCourier {
    private static int courierId;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterClass
    public static void deleteCourier(){
        courierId = given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(200).and().extract().body().path("id");
        given().and().when().delete("api/v1/courier/" + courierId).then().statusCode(200).and().assertThat().body("ok",equalTo(true));
    }


    @DisplayName("Успешное создание курьера, проверка кода успешного ответа и тела ответа об успешном создании курьера")
    @Test
    public void testSuccessfulCreateCourier(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224\",\n" +
                        "    \"firstName\": \"kuku01\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(201).and().assertThat().body("ok",equalTo(true));
    }

    @DisplayName("Проверка, что нельзя создать двух одинаковых курьеров")
    @Test
    public void testErrorWhenCreateTwoIdenticalCourier(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224\",\n" +
                        "    \"firstName\": \"kuku01\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(409).and().assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @DisplayName("Проверка, что при создании курьера необходимо передать все обязательные поля")
    @Test
    public void testWhenAllFieldsIsRequired(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"password\": \"12343224\",\n" +
                        "    \"firstName\": \"kuku01\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"));
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"firstName\": \"kuku01\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для создания учетной записи"));
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(409).and().assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
        //тут получается ошибка документации, так как поле firstName не указано как необязательное ,а проверка проходит только по логину и паролю
    }

    @DisplayName("Проверка, что если попробовать создать с существующим логином , но другим паролем и именем , то будет ошибка")
    @Test
    public void testErrorWhenCreateIdenticalCourierLogin(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"1234322454554\",\n" +
                        "    \"firstName\": \"kuku014545\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(409).and().assertThat().body("message",equalTo("Этот логин уже используется. Попробуйте другой."));
    }

}
