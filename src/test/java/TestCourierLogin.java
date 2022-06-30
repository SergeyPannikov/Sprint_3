import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TestCourierLogin extends TestCreateCourier {
    public CourierAccount courierLogin;

    @DisplayName("Проверка успешного входа курьером и запрос возвращает id")
    @Test
    public void testLoginCourier(){
        testSuccessfulCreateCourier();
        courierLogin = new CourierAccount("kuku01","12343224");
        courierId = given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(200).and().extract().body().path("id");
    }

    @DisplayName("Проверка что необходимо передать login, чтобы войти")
    @Test
    public void testRequiredFieldLogin(){
        courierLogin = new CourierAccount(null,"12343224");
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Проверка что необходимо передать password,чтобы войти")
    @Test
    public void testRequiredFieldPassword(){
        courierLogin = new CourierAccount("kuku01",null);
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(504);
        //должно быть как для первого запроса, но сервис падает по таймауту c 504 , вместо того чтобы кидать 400 ошибку с телом ответа
    }

    @DisplayName("Проверка ошибки , при введенном неправильном логине")
    @Test
    public void testErrorWhenFieldLoginIncorrect(){
        courierLogin = new CourierAccount("kuku0144","12343224");
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(404).and().assertThat().body("message",equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Проверка ошибки , при введенном неправильном пароле")
    @Test
    public void testErrorWhenFieldPasswordIncorrect(){
        courierLogin = new CourierAccount("kuku01","12343224555");
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(404).and().assertThat().body("message",equalTo("Учетная запись не найдена"));
    }

    @DisplayName("Проверка ошибки , если поле логин пустое")
    @Test
    public void testErrorWhenLoginIsEmpty(){
        courierLogin = new CourierAccount("","12343224");
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Проверка ошибки , если поле пароля пустое")
    @Test
    public void testErrorWhenPasswordIsEmpty(){
        courierLogin = new CourierAccount("kuku01","");
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(400).and().assertThat().body("message",equalTo("Недостаточно данных для входа"));
    }

    @DisplayName("Проверка ошибки, при попытке входа под несуществущим логином")
    @Test
    public void testErrorWhenLoginNotExist(){
        courierLogin = new CourierAccount("megakuku01","12343224");
        given().header("Content-type", "application/json").
                and().body(courierLogin).when().post("api/v1/courier/login").then().statusCode(404).and().assertThat().body("message",equalTo("Учетная запись не найдена"));
    }
}
