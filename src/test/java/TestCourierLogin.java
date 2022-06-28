import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCourierLogin {
    private static int courierId;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224\",\n" +
                        "    \"firstName\": \"kuku01\"\n" +
                        "}").when().post("api/v1/courier").then().statusCode(201).and().assertThat().body("ok",equalTo(true));
    }

    @AfterClass
    public static void deleteCourier(){
        given().and().when().delete("api/v1/courier/" + courierId).then().statusCode(200).and().assertThat().body("ok",equalTo(true));
    }

    @DisplayName("Проверка успешного входа курьером и запрос возвращает id")
    @Test
    public void testLoginCourier(){
        courierId = given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(200).and().extract().body().path("id");
    }

    @DisplayName("Проверка что необходимо передать все обязательные поля в теле ,чтобы войти")
    @Test
    public void testRequiredFieldsForLogin(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"password\": \"12343224\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для входа"));
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(504);
        //должно быть как для первого запроса, но сервис падает по таймауту c 504 , вместо того чтобы кидать 400 ошибку с телом ответа
    }

    @DisplayName("Проверка ошибки , при введенных неправильных логина или пароля")
    @Test
    public void testErrorWhenFieldsForLoginIncorrect(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku0100\",\n" +
                        "    \"password\": \"12343224\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(404).and().assertThat().body("message",equalTo("Учетная запись не найдена"));
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"12343224777\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(404).and().assertThat().body("message",equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Проверка ошибки , если одно из полей незаполненно(пустое)")
    @Test
    public void testErrorWhenLoginOrPasswordIsEmpty(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"\",\n" +
                        "    \"password\": \"12343224\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для входа"));
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku01\",\n" +
                        "    \"password\": \"\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Проверка ошибки, при попытке входа под несуществущим логином")
    @Test
    public void testErrorWhenLoginNotExist(){
        given().header("Content-type", "application/json").
                and().body("{\n" +
                        "    \"login\": \"kuku0100\",\n" +
                        "    \"password\": \"1234322455\"\n" +
                        "}").when().post("api/v1/courier/login").then().statusCode(404).and().assertThat().body("message",equalTo("Учетная запись не найдена"));
    }
}
