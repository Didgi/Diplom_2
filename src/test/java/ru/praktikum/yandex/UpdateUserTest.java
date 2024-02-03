package ru.praktikum.yandex;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import ru.praktikum.yandex.client.UserClient;
import ru.praktikum.yandex.step.ClientSteps;

import static org.apache.http.HttpStatus.*;
import static ru.praktikum.yandex.config.Configs.*;

public class UpdateUserTest {
    private static ClientSteps clientSteps;
    private static final Faker faker = new Faker();
    private static final String existedClientEmail = faker.internet().emailAddress();
    private static final String originalEmail = faker.internet().emailAddress();
    private static final String newEmail = faker.internet().emailAddress();
    private static final String password = faker.internet().password(1, 10, true, true);
    private static final String originalName = faker.name().name();
    private static final String newName = faker.name().name();

    @BeforeClass
    public static void setUp() {
        clientSteps = new ClientSteps(new UserClient());
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(clientSteps.createClient(existedClientEmail, password, originalName)));
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(clientSteps.createClient(originalEmail, password, originalName)));
    }

    @Test
    @DisplayName("Изменение email и имени клиента на уникальные с валидным токеном")
    @Description("В этом тесте проверяется изменение email и имени клиента на другие значения с передачей обязательных валидных данных и валидным токеном, где ожидается 200 код и проверяются все поля в ответе")
    public void patchClientPositiveNewEmailNameShowsOk() {
        ValidatableResponse response = clientSteps.patchClient(newEmail, newName, clientSteps.getToken());
        clientSteps.checkStatusCode(response, SC_OK);
        clientSteps.checkResponseBodyAfterPatching(true, response, newEmail, newName, null);

    }

    @Test
    @DisplayName("Изменение email и имени клиента на уникальные с невалидным токеном")
    @Description("В этом тесте проверяется изменение email и имени клиента на другие значения с передачей обязательных валидных данных, но с пустым токеном, где ожидается 200 код и проверяются все поля в ответе")
    public void patchClientNegativeEmptyTokenShowsError() {
        ValidatableResponse response = clientSteps.patchClient(newEmail, newName, "");
        clientSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        clientSteps.checkResponseBodyAfterPatching(false, response, null, null, PATCH_WITHOUT_TOKEN_DETAILED_ERROR_TEXT_401);

    }

    @Test
    @DisplayName("Изменение email и имени клиента на точно такие же")
    @Description("В этом тесте проверяется изменение email и имени клиента на точно такие же данные с передачей обязательных валидных данных, где ожидается 200 код и проверяются все поля в ответе")
    public void patchClientPositiveOldEmailNameShowsOk() {
        ValidatableResponse response = clientSteps.patchClient(originalEmail, originalName, clientSteps.getToken());
        clientSteps.checkStatusCode(response, SC_OK);
        clientSteps.checkResponseBodyAfterPatching(true, response, originalEmail, originalName, null);

    }

    @Test
    @DisplayName("Изменение email клиента на существующий от другого пользователя")
    @Description("В этом тесте проверяется изменение email на значение от другого пользователя, где ожидается 403 код и проверяется сообщение об ошибке")
    public void patchClientNegativeExistedEmailShowsOk() {
        ValidatableResponse response = clientSteps.patchClient(existedClientEmail, originalName, clientSteps.getToken());
        clientSteps.checkStatusCode(response, SC_FORBIDDEN);
        clientSteps.checkResponseBodyAfterPatching(false, response, null, null, PATCH_DETAILED_ERROR_TEXT_403);

    }

    @AfterClass
    public static void tearDown() {
        clientSteps.clearTestClientsData();
    }

}
