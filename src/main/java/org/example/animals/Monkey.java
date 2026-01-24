package org.example.animals;


public class Monkey extends Animal {

    public Monkey(String name) {
        super(name, "Monkey");
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " кричит.");
    }

    @Override
    public void performDailyActivity() {
        System.out.println(getName() + " прыгает с ветки на ветку.");
    }
}


