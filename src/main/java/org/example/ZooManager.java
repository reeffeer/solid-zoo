package org.example;

import org.example.animals.Animal;
import org.example.employees.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер зоопарка - централизованное хранилище животных и сотрудников.
 * SRP: отвечает только за хранение и базовое управление коллекциями.
 */
public class ZooManager {
    
    private final List<Animal> animals;
    private final List<Employee> employees;
    
    public ZooManager() {
        this.animals = new ArrayList<>();
        this.employees = new ArrayList<>();
    }
    
    /**
     * Добавляет животное в зоопарк.
     * При добавлении животное автоматически попадает в список.
     */
    public void addAnimal(Animal animal) {
        animals.add(animal);
        System.out.println("✓ Добавлено животное: " + animal.getName() + " (" + animal.getSpecies() + ")");
    }
    
    /**
     * Добавляет сотрудника в зоопарк.
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
        System.out.println("✓ Добавлен сотрудник: " + employee.getName() + " (" + employee.getRole() + ")");
    }
    
    /**
     * Возвращает список всех животных.
     */
    public List<Animal> getAnimals() {
        return new ArrayList<>(animals);
    }
    
    /**
     * Возвращает список всех сотрудников.
     */
    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }
    
    /**
     * Получает список животных конкретного вида.
     */
    public List<Animal> getAnimalsBySpecies(String species) {
        return animals.stream()
                .filter(a -> a.getSpecies().equalsIgnoreCase(species))
                .collect(Collectors.toList());
    }
}

