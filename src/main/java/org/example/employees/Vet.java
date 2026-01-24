package org.example.employees;

import org.example.animals.Animal;
import org.example.care.Treatable;

/**
 * Ветеринар: отвечает только за лечение животных.
 * Реализует только нужные интерфейсы (ISP).
 */
public class Vet extends Employee implements Treatable {

    public Vet(String name) {
        super(name, "Ветеринар");
    }

    @Override
    public void treat(Animal animal) {
        System.out.println(getName() + " проводит осмотр и лечение " + animal.getName() + " (" + animal.getSpecies()
                + ").");
    }
}


