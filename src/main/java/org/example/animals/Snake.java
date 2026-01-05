package org.example.animals;


public class Snake extends Animal {

    public Snake(String name) {
        super(name, "Snake");
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + " шипит.");
    }

    @Override
    public void performDailyActivity() {
        System.out.println(getName() + " греется под лампой.");
    }
}


