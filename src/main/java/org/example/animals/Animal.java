package org.example.animals;

/**
 * Базовый абстрактный класс животного.
 * SRP: Отвечает только за общие данные и базовое поведение животного.
 */
public abstract class Animal {

    private final String name;
    private final String species;

    protected Animal(String name, String species) {
        this.name = name;
        this.species = species;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    /**
     * Типичный звук животного (рык, чириканье и т.п.).
     */
    public abstract void makeSound();

    /**
     * Характерное действие животного (охота, полёт и т.п.).
     */
    public abstract void performDailyActivity();
}
