# Diplom_2
<h3>Описание:</h3>
1. В данном проекте реализовано тестирование ручек API для [сайта](https://stellarburgers.nomoreparties.site/);

2. Документация [API](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf);

<h3>Технологии</h3>
1. Проект использует Java 11;

2. Используемые библиотеки для реализации api тестов добавлены в pom.xml: JUnit 4, Rest-Assured, Gson, Lombok, JavaFaker, Allure, Maven;

<h3>Запуск:</h3>
1. Чтобы запустить тесты, необходимо выполнить команду: mvn clean test;

2. Чтобы просмотреть собранный allure report по выполненному прогону тестов, необходимо выполнить команду: mvn allure:serve
