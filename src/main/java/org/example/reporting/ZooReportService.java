package org.example.reporting;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.animals.Animal;
import org.example.care.Cleanable;
import org.example.care.Feedable;
import org.example.care.Treatable;
import org.example.employees.Employee;

/**
 * Отчётный модуль по животным.
 * DIP: зависит от абстракции List<Animal>, а не от конкретных реализаций животных.
 */
public class ZooReportService {

    public String buildShortSummary(List<Animal> animals) {
        StringBuilder builder = new StringBuilder();
        builder.append("Общее количество животных: ").append(animals.size()).append("\n");

        Map<String, Long> bySpecies = animals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));
        
        builder.append("Распределение по видам:\n");
        bySpecies.forEach((species, count) -> 
            builder.append("  ").append(species).append(": ").append(count).append("\n"));

        return builder.toString();
    }

    public String buildFeedingSchedule(List<Animal> animals) {
        StringBuilder builder = new StringBuilder();
        builder.append("Расписание кормления на сегодня:\n");

        LocalTime baseTime = LocalTime.of(9, 0);
        int counter = 0;
        for (Animal animal : animals) {
            LocalTime time = baseTime.plusMinutes(counter * 30L);
            builder.append(time).append(" - ").append(animal.getName()).append(" (")
                    .append(animal.getSpecies()).append(")").append("\n");
            counter++;
        }

        return builder.toString();
    }

    /**
     * Строит расписание уборки вольеров на сегодня.
     */
    public String buildCleaningSchedule(List<Animal> animals) {
        StringBuilder builder = new StringBuilder();
        builder.append("Расписание уборки вольеров на сегодня:\n");

        LocalTime baseTime = LocalTime.of(14, 0); // Уборка начинается с 14:00
        int counter = 0;
        for (Animal animal : animals) {
            LocalTime time = baseTime.plusMinutes(counter * 30L);
            builder.append(time).append(" - ").append(animal.getName()).append(" (")
                    .append(animal.getSpecies()).append(")").append("\n");
            counter++;
        }

        return builder.toString();
    }

    /**
     * Строит расписание медицинских осмотров на сегодня.
     */
    public String buildMedicalSchedule(List<Animal> animals) {
        StringBuilder builder = new StringBuilder();
        builder.append("Расписание медицинских осмотров на сегодня:\n");

        LocalTime baseTime = LocalTime.of(11, 0); // Медосмотры начинаются с 11:00
        int counter = 0;
        for (Animal animal : animals) {
            LocalTime time = baseTime.plusMinutes(counter * 45L); // Медосмотр занимает больше времени
            builder.append(time).append(" - ").append(animal.getName()).append(" (")
                    .append(animal.getSpecies()).append(")").append("\n");
            counter++;
        }

        return builder.toString();
    }

    /**
     * Строит информацию о сотрудниках зоопарка.
     */
    public String buildEmployeesInfo(List<Employee> employees) {
        StringBuilder builder = new StringBuilder();
        builder.append("=== Информация о сотрудниках зоопарка ===\n");
        builder.append("Общее количество сотрудников: ").append(employees.size()).append("\n\n");

        if (employees.isEmpty()) {
            builder.append("В зоопарке пока нет сотрудников.\n");
            return builder.toString();
        }

        builder.append("Список сотрудников:\n");
        for (Employee employee : employees) {
            builder.append("\n  Имя: ").append(employee.getName()).append("\n");
            builder.append("  Должность: ").append(employee.getRole()).append("\n");
            
            List<String> responsibilities = new ArrayList<>();
            if (employee instanceof Feedable) {
                responsibilities.add("Кормление животных");
            }
            if (employee instanceof Cleanable) {
                responsibilities.add("Уборка вольеров");
            }
            if (employee instanceof Treatable) {
                responsibilities.add("Медицинский осмотр и лечение");
            }
            
            if (!responsibilities.isEmpty()) {
                builder.append("  Обязанности: ").append(String.join(", ", responsibilities)).append("\n");
            }
        }

        return builder.toString();
    }
}


