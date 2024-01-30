package ru.praktikum.yandex;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
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

public class CreateOrderTest {
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
        ValidatableResponse response = clientSteps.createClient(email, password, name);
        clientSteps.addClientsDataToClear(clientSteps.setAccessToken(response));
    }

    @Test
    @DisplayName("Создание заказа с одним любым ингредиентом и валидным токеном")
    @Description("В этом тесте проверяется создание заказа с одним любым ингредиентом, где проверяется код ответа, имя бургера и его ингредиенты")
    public void createOrderPositiveRandomIngredientShowsOk() {
        String[] ingredients = new String[]{orderSteps.getRandomIngredient()};
        ValidatableResponse response = orderSteps.createOrder(ingredients, clientSteps.getToken());
        orderSteps.checkStatusCode(response, SC_OK);
        orderSteps.checkResponseBodyOrder(true, response, ingredients, null, name, email);

    }

    @Test
    @DisplayName("Создание заказа с всеми ингредиентами и валидным токеном")
    @Description("В этом тесте проверяется создание заказа с всеми ингредиентами, где проверяется код ответа, имя бургера и его ингредиенты")
    public void createOrderPositiveAllIngredientsShowsOk() {
        String[] ingredients = orderSteps.getAllIngredient();
        ValidatableResponse response = orderSteps.createOrder(ingredients, clientSteps.getToken());
        orderSteps.checkStatusCode(response, SC_OK);
        orderSteps.checkResponseBodyOrder(true, response, ingredients, null, name, email);

    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и валидным токеном")
    @Description("В этом тесте проверяется создание заказа без передачи ингредиентов, где проверяется код ответа и сообщение об ошибке")
    public void createOrderNegativeWithoutIngredientsShowsOk() {
        ValidatableResponse response = orderSteps.createOrder(null, clientSteps.getToken());
        orderSteps.checkStatusCode(response, SC_BAD_REQUEST);
        orderSteps.checkResponseBodyOrder(false, response, null, CREATE_ORDER_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_400, null, null);

    }

    @Test
    @DisplayName("Создание заказа c пустой строкой ингредиента и валидным токеном")
    @Description("В этом тесте проверяется создание заказа c пустой строкой ингредиента, где проверяется код ответа и сообщение об ошибке")
    public void createOrderNegativeEmptyIngredientShowsOk() {
        String[] ingredients = new String[]{""};
        ValidatableResponse response = orderSteps.createOrder(ingredients, clientSteps.getToken());
        orderSteps.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);

    }

    @Test
    @DisplayName("Создание заказа с невалидным хэшем ингредиента и валидным токеном")
    @Description("В этом тесте проверяется создание заказа с передачей невалидного хэша ингредиента, где проверяется код ответа и сообщение об ошибке")
    public void createOrderNegativeWrongHashIngredientShowsOk() {
        String[] ingredients = new String[]{faker.internet().password()};
        ValidatableResponse response = orderSteps.createOrder(ingredients, clientSteps.getToken());
        orderSteps.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);

    }

    @Test
    @DisplayName("Создание заказа с всеми ингредиентами, но с пустым токеном")
    @Description("В этом тесте проверяется создание заказа с передачей всех ингредиентов, но с пустым токеном, где проверяется код ответа и сообщение об ошибке")
    @Issue("BUG-1")
    //Тест падает, потому что ожидается, что заказ не будет создан без передачи/пустой токен.
    //Требование: Только авторизованные пользователи могут делать заказы. Структура эндпоинтов
    //не меняется, но нужно предоставлять токен при запросе к серверу в поле Authorization.
    public void createOrderNegativeEmptyTokenShowsOk() {
        String[] ingredients = orderSteps.getAllIngredient();
        ValidatableResponse response = orderSteps.createOrder(ingredients, "");
        orderSteps.checkStatusCode(response, SC_UNAUTHORIZED);

    }

    @Test
    @DisplayName("Создание заказа с всеми ингредиентами, но с несуществующим токеном")
    @Description("В этом тесте проверяется создание заказа с передачей всех ингредиентов, но с несуществующим токеном, где проверяется код ответа и сообщение об ошибке")
    @Issue("BUG-1")
    //Тест падает, потому что ожидается, что заказ не будет создан без существующего токена.
    //Требование: Только авторизованные пользователи могут делать заказы. Структура эндпоинтов
    //не меняется, но нужно предоставлять токен при запросе к серверу в поле Authorization.
    public void createOrderNegativeRandomNotExistedTokenShowsOk() {
        String[] ingredients = orderSteps.getAllIngredient();
        ValidatableResponse response = orderSteps.createOrder(ingredients, faker.internet().password());
        orderSteps.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    @After
    public void tearDown() {
        clientSteps.clearTestClientsData();
    }
}
