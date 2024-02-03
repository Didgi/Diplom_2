package ru.praktikum.yandex.client;

import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import ru.praktikum.yandex.dto.CreateUserDto;
import ru.praktikum.yandex.dto.LoginUserDto;
import ru.praktikum.yandex.dto.PatchUserDto;

import static ru.praktikum.yandex.config.Configs.*;

public class UserClient extends RestClient {

    public Response createClient(CreateUserDto createUserDto) {
        return getDefaultRequestSpecificationWithoutToken()
                .body(createUserDto)
                .when()
                .post(CREATE_USER);

    }

    public Response loginClient(LoginUserDto loginUserDto) {
        return getDefaultRequestSpecificationWithoutToken()
                .body(loginUserDto)
                .when()
                .post(LOGIN_USER);
    }

    public Response deleteClient(String token) {
        return getRequestSpecificationWithToken(token)
                .when()
                .delete(USER);
    }

    public Response patchClient(PatchUserDto patchUserDto, String token) {
        return getRequestSpecificationWithToken(token)
                .body(patchUserDto)
                .when()
                .patch(USER);
    }
}
