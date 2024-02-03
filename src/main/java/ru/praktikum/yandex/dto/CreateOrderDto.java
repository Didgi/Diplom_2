package ru.praktikum.yandex.dto;

import lombok.Data;

@Data
public class CreateOrderDto {
    String[] ingredients;

    public CreateOrderDto(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
