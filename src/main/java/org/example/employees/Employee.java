package org.example.employees;

import org.example.care.CareCapable;

/**
 * Базовый класс сотрудника зоопарка.
 * Не содержит логики ухода за животными, только общие данные.
 */
public abstract class Employee implements CareCapable {

    private final String name;
    private final String role;

    protected Employee(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
