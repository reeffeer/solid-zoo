package org.example.employees;

import org.example.animals.Animal;
import org.example.care.Cleanable;
import org.example.care.Feedable;

/**
 * Смотритель зоопарка: кормление и уборка.
 * Реализует только нужные интерфейсы (ISP).
 */
public class ZooKeeper extends Employee implements Feedable, Cleanable {

    public ZooKeeper(String name) {
        super(name, "Смотритель зоопарка");
    }

    @Override
    public void feed(Animal animal) {
        System.out.println(getName() + " кормит " + animal.getName() + " (" + animal.getSpecies() + ").");
    }

    @Override
    public void clean(Animal animal) {
        System.out
                .println(getName() + " убирает вольер у " + animal.getName() + " (" + animal.getSpecies() + ").");
    }
}


