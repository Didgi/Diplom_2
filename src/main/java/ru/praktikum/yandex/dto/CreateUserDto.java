package ru.praktikum.yandex.dto;

import lombok.*;

@Data
public class CreateUserDto {
    private String email;
    private String password;
    private String name;
}
