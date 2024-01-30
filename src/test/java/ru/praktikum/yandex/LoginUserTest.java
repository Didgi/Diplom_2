package ru.praktikum.yandex;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.yandex.client.UserClient;
import ru.praktikum.yandex.step.ClientSteps;

import static org.apache.http.HttpStatus.*;
import static ru.praktikum.yandex.config.Configs.*;

public class LoginUserTest {
    private ClientSteps clientSteps;
    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password(5, 10, true, true);
    String name = faker.name().name();

    @Before
    public void setUp() {
        clientSteps = new ClientSteps(new UserClient());
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(clientSteps.createClient(email, password, name)));
    }

    @Test
    @DisplayName("Логин клиента со всеми полями")
    @Description("В этом тесте проверяется логин клиентом с валидными данными, где ожидается 200 код и все необходимые поля внутри")
    public void loginUserPositiveAllFieldsShowsOk() {
        ValidatableResponse response = clientSteps.loginClient(email, password);
        clientSteps.checkStatusCode(response, SC_OK);
        clientSteps.checkResponseBody(true, response, email, name, null);

    }

    @Test
    @DisplayName("Логин клиента с указанием password другим регистром")
    @Description("В этом тесте проверяется логин клиентом с password указанном в другом регистре, где ожидается 401 код и проверяется сообщение об ошибке")
    public void loginUserNegativeDiffRegisterPasswordShowsError() {
        ValidatableResponse response = clientSteps.loginClient(email, password.toUpperCase());
        clientSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        clientSteps.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);

    }

    @Test
    @DisplayName("Логин клиента без передачи email")
    @Description("В этом тесте проверяется логин клиентом без передачи email, где ожидается 401 код и проверяется сообщение об ошибке")
    public void loginUserNegativeWithoutEmailShowsError() {
        ValidatableResponse response = clientSteps.loginClient(null, password);
        clientSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        clientSteps.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);

    }

    @Test
    @DisplayName("Логин клиента с пустым email")
    @Description("В этом тесте проверяется логин клиентом с пустым email, где ожидается 401 код и проверяется сообщение об ошибке")
    public void loginUserNegativeEmptyEmailShowsError() {
        ValidatableResponse response = clientSteps.loginClient("", password);
        clientSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        clientSteps.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);

    }

    @Test
    @DisplayName("Логин клиента без передачи password")
    @Description("В этом тесте проверяется логин клиентом без передачи password, где ожидается 401 код и проверяется сообщение об ошибке")
    public void loginUserNegativeWithoutPasswordShowsError() {
        ValidatableResponse response = clientSteps.loginClient(email, null);
        clientSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        clientSteps.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);

    }

    @Test
    @DisplayName("Логин клиента с пустым password")
    @Description("В этом тесте проверяется логин клиентом с пустым password, где ожидается 401 код и проверяется сообщение об ошибке")
    public void loginUserNegativeEmptyPasswordShowsError() {
        ValidatableResponse response = clientSteps.loginClient(email, "");
        clientSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        clientSteps.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);

    }


    @After
    public void tearDown() {
        clientSteps.clearTestClientsData();
    }
}
