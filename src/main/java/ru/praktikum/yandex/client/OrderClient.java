package ru.praktikum.yandex.client;

import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import ru.praktikum.yandex.dto.CreateOrderDto;

import static ru.praktikum.yandex.config.Configs.*;

public class OrderClient extends RestClient {
    public Response createOrder(CreateOrderDto createOrderDto, String token) {
        return getRequestSpecificationWithToken(token)
                .body(createOrderDto)
                .when()
                .post(ORDER);
    }

    public Response getUserOrder(String token) {
        return getRequestSpecificationWithToken(token)
                .when()
                .get(ORDER);
    }

    public Response getIngredients() {
        return getDefaultRequestSpecificationWithoutToken()
                .when()
                .get(INGREDIENTS);
    }
}
