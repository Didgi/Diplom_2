package ru.praktikum.yandex;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.yandex.client.UserClient;
import ru.praktikum.yandex.step.ClientSteps;

import static org.apache.http.HttpStatus.*;
import static ru.praktikum.yandex.config.Configs.*;

public class CreateUserTest {

    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password(1, 10, true, true);
    String name = faker.name().name();
    private static ClientSteps clientSteps;

    @Before
    public void setUp() {
        clientSteps = new ClientSteps(new UserClient());
    }

    @Test
    @DisplayName("Создание клиента с всеми обязательными полями")
    @Description("В этом тесте проверяется создание клиента с передачей обязательных валидных данных, где ожидается 200 код и проверяются все поля в ответе")
    public void createClientPositiveAllFieldsShowsOk() {
        ValidatableResponse response = clientSteps.createClient(email, password, name);
        clientSteps.checkStatusCode(response, SC_OK);
        clientSteps.checkResponseBody(true, response, email, name, null);
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(response));
    }

    @Test
    @DisplayName("Повторное создание клиента точно такого же клиента")
    @Description("В этом тесте проверяется повторное создание клиента с передачей обязательных валидных данных, где ожидается 403 код и проверяется сообщение об ошибке")
    public void doubleCreateClientNegativeAllFieldsShowsError() {
        ValidatableResponse firstResponse = clientSteps.createClient(email, password, name);
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(firstResponse));
        ValidatableResponse response = clientSteps.createClient(email, password, name);
        clientSteps.checkStatusCode(response, SC_FORBIDDEN);
        clientSteps.checkResponseBody(false, response, null, null, CREATE_USER_EXIST_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента с пустым email")
    @Description("В этом тесте проверяется создание клиента, где передаётся пустое поле email, а остальные поля заполнены. Ожидается 403 код и проверяется сообщение об ошибке")
    public void createClientNegativeEmptyEmailShowsError() {
        ValidatableResponse response = clientSteps.createClient("", password, name);
        clientSteps.checkStatusCode(response, SC_FORBIDDEN);
        clientSteps.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента с пустым password")
    @Description("В этом тесте проверяется создание клиента, где передаётся пустое поле password, а остальные поля заполнены. Ожидается 403 код и проверяется сообщение об ошибке")
    public void createClientNegativeEmptyPasswordShowsError() {
        ValidatableResponse response = clientSteps.createClient(email, "", name);
        clientSteps.checkStatusCode(response, SC_FORBIDDEN);
        clientSteps.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента с пустым name")
    @Description("В этом тесте проверяется создание клиента, где передаётся пустое поле name, а остальные поля заполнены. Ожидается 403 код и проверяется сообщение об ошибке")
    public void createClientNegativeEmptyNameShowsError() {
        ValidatableResponse response = clientSteps.createClient(email, password, "");
        clientSteps.checkStatusCode(response, SC_FORBIDDEN);
        clientSteps.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента без передачи полей")
    @Description("В этом тесте проверяется создание клиента, где отсутствуют все поля. Ожидается 403 код и проверяется сообщение об ошибке")
    public void createClientNegativeWithoutAllFieldsShowsError() {
        ValidatableResponse response = clientSteps.createClient(null, null, null);
        clientSteps.checkStatusCode(response, SC_FORBIDDEN);
        clientSteps.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @AfterClass
    public static void tearDown() {
        clientSteps.clearTestClientsData();
    }


}
