package ru.praktikum.yandex.step;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import lombok.Data;
import ru.praktikum.yandex.client.UserClient;
import ru.praktikum.yandex.dto.CreateUserDto;
import ru.praktikum.yandex.dto.LoginUserDto;
import ru.praktikum.yandex.dto.PatchUserDto;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static ru.praktikum.yandex.config.Configs.*;

@Data
public class ClientSteps {
    private final UserClient userClient;
    private String token = null;
    static List<String> clientsData = new ArrayList<>();

    public ClientSteps(UserClient userClient) {
        this.userClient = userClient;
    }

    @Step("Создание клиента")
    public ValidatableResponse createClient(String email, String password, String name) {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setEmail(email);
        createUserDto.setPassword(password);
        createUserDto.setName(name);
        return userClient.createClient(createUserDto).then();
    }

    @Step("Логин клиентом")
    public ValidatableResponse loginClient(String email, String password) {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail(email);
        loginUserDto.setPassword(password);
        return userClient.loginClient(loginUserDto).then();

    }

    @Step("Удаление клиента")
    public ValidatableResponse deleteClient(String token) {
        return userClient.deleteClient(token).then();

    }

    @Step("Изменение клиента")
    public ValidatableResponse patchClient(String email, String name, String token) {
        PatchUserDto patchUserDto = new PatchUserDto();
        patchUserDto.setEmail(email);
        patchUserDto.setName(name);
        return userClient.patchClient(patchUserDto, token).then();

    }

    @Step("Проверка статус кода в ответе")
    public void checkStatusCode(ValidatableResponse response, int code) {
        response.statusCode(code);
    }

    @Step("Проверка тела ответа")
    public void checkResponseBody(boolean isTestPositive, ValidatableResponse response, String email, String name, String detailedError) {
        if (isTestPositive) {
            response.assertThat().body("success", is(true))
                    .and().body("accessToken", notNullValue())
                    .and().body("refreshToken", notNullValue())
                    .and().body("user.email", equalTo(email))
                    .and().body("user.name", equalTo(name));
        } else {
            response.assertThat().body("success", is(false))
                    .and().body("message", equalTo(detailedError));
        }
    }

    @Step("Проверка тела ответа после обновления")
    public void checkResponseBodyAfterPatching(boolean isTestPositive, ValidatableResponse response, String email, String name, String detailedError) {
        if (isTestPositive) {
            response.assertThat().body("success", is(true))
                    .and().body("user.email", equalTo(email))
                    .and().body("user.name", equalTo(name));
        } else {
            response.assertThat().body("success", is(false))
                    .and().body("message", equalTo(detailedError));
        }
    }

    @Step("Проверка ответа при удалении клиента")
    public void checkResponseBodyAfterDeletion(ValidatableResponse response) {
        response.assertThat().body("success", is(true))
                .and().body("message", equalTo(DELETE_CONFIRM_MESSAGE));
    }

    @Step("Получение token из ответа")
    public String setAccessToken(ValidatableResponse response) {
        token = response.assertThat().extract().path("accessToken");
        return token;

    }

    @Step("Удаление созданных клиентов")
    public void clearTestClientsData() {
        for (String token : clientsData) {
            ValidatableResponse response = deleteClient(token);
            checkStatusCode(response, SC_ACCEPTED);
            checkResponseBodyAfterDeletion(response);
        }
        clientsData.clear();
    }

    public void addClientsDataToClear(String token) {
        clientsData.add(token);
    }
}
