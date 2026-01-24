package org.example.animals;


public class Parrot extends Animal {

    public Parrot(String name) {
        super(name, "Parrot");
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " говорит: \"Привет!\"");
    }

    @Override
    public void performDailyActivity() {
        System.out.println(getName() + " перелетает с жердочки на жердочку.");
    }
}


