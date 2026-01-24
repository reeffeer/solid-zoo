package org.example.animals;


public class Wolf extends Animal {

    public Wolf(String name) {
        super(name, "Wolf");
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " рычит.");
    }

    @Override
    public void performDailyActivity() {
        System.out.println(getName() + " лениво гуляет по вольеру и наблюдает за посетителями.");
    }
}


