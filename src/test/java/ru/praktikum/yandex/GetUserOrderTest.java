package ru.praktikum.yandex;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.yandex.client.OrderClient;
import ru.praktikum.yandex.client.UserClient;
import ru.praktikum.yandex.step.ClientSteps;
import ru.praktikum.yandex.step.OrderSteps;

import static org.apache.http.HttpStatus.*;
import static ru.praktikum.yandex.config.Configs.*;

public class GetUserOrderTest {
    private OrderSteps orderSteps;
    private ClientSteps clientSteps;
    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password(1, 10, true, true);
    String name = faker.name().name();

    @Before
    public void setUp() {
        orderSteps = new OrderSteps(new OrderClient());
        clientSteps = new ClientSteps(new UserClient());
        ValidatableResponse responseClient = clientSteps.createClient(email, password, name);
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(responseClient));
        ValidatableResponse firstResponseOrder = orderSteps.createOrder(orderSteps.getAllIngredient(), clientSteps.getToken());
        ValidatableResponse secondResponseOrder = orderSteps.createOrder(orderSteps.getAllIngredient(), clientSteps.getToken());
        orderSteps.getOrdersForCompare(firstResponseOrder);
        orderSteps.getOrdersForCompare(secondResponseOrder);

    }

    @Test
    @DisplayName("Проверка получения списка заказов пользователя с валидным токеном")
    @Description("В этом тесте проверяется получение заказов пользователя с валидным токеном, где проверяется код ответа и заказы созданные пользователем")
    public void getUserOrdersPositiveShowsOk() {
        ValidatableResponse response = orderSteps.getUserOrders(clientSteps.getToken()).then();
        orderSteps.checkStatusCode(response, SC_OK);
        orderSteps.checkResponseBodyUser(true, response, orderSteps.getOrderList(), null);
    }

    @Test
    @DisplayName("Проверка получения списка заказов пользователя с невалидным токеном")
    @Description("В этом тесте проверяется получение заказов пользователя с не валидным токеном, где проверяется код ответа и сообщение об ошибке")
    public void getUserOrdersNegativeNotExistedTokenShowsOk() {
        ValidatableResponse response = orderSteps.getUserOrders(faker.internet().password()).then();
        orderSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        orderSteps.checkResponseBodyUser(false, response, null, GET_USER_ORDER_DETAILED_ERROR_TEXT_401);
    }

    @Test
    @DisplayName("Проверка получения списка заказов пользователя с пустым токеном")
    @Description("В этом тесте проверяется получение заказов пользователя с пустым токеном, где проверяется код ответа и сообщение об ошибке")
    public void getUserOrdersNegativeEmptyTokenShowsOk() {
        ValidatableResponse response = orderSteps.getUserOrders(faker.internet().password()).then();
        orderSteps.checkStatusCode(response, SC_UNAUTHORIZED);
        orderSteps.checkResponseBodyUser(false, response, null, GET_USER_ORDER_DETAILED_ERROR_TEXT_401);
    }

    @After
    public void tearDown() {
        clientSteps.clearTestClientsData();
    }
}
