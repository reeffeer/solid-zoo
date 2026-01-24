package org.example;

import java.util.List;
import java.util.Scanner;

import org.example.animals.Animal;
import org.example.employees.Employee;
import org.example.reporting.ZooReportService;

/**
 * Интерактивный консольный интерфейс для управления зоопарком.
 */
public class Main {

    private static ZooManager zooManager;
    private static ZooReportService reportService;

    public static void main(String[] args) {
        zooManager = new ZooManager();
        reportService = new ZooReportService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Система управления зоопарком ===");
        System.out.println("Введите 'info' для списка доступных команд.");
        System.out.println();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+", 3);
            String command = parts[0].toLowerCase();

            switch (command) {
                case "add" -> handleAddCommand(parts);
                case "stat" -> handleStatCommand();
                case "sched" -> handleSchedCommand();
                case "show" -> handleShowCommand(parts);
                case "employees", "emps" -> handleEmployeesCommand();
                case "info" -> handleInfoCommand();
                case "exit", "quit" -> {
                    System.out.println("До свидания!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Неизвестная команда. Введите 'info' для списка команд.");
            }
        }
    }

    /**
     * Обработка команды добавления (add animal/employee).
     */
    private static void handleAddCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Использование: add <-a|-e> <тип> <имя>");
            return;
        }

        String type = parts[1].toLowerCase();

        if ("-a".equals(type)) {
            if (parts.length < 3) {
                System.out.println("Использование: add -a <тип> <имя>");
                List<String> availableTypes = ReflectionUtils.getAvailableAnimalTypes();
                System.out.println("Доступные типы: " + String.join(", ", availableTypes));
                return;
            }

            String[] animalParts = parts[2].split("\\s+", 2);
            if (animalParts.length < 2) {
                System.out.println("Использование: add -a <тип> <имя>");
                return;
            }

            String animalType = animalParts[0].toLowerCase();
            String name = animalParts[1];

            Animal animal = ReflectionUtils.createAnimalByType(animalType, name);
            if (animal != null) {
                zooManager.addAnimal(animal);
            } else {
                System.out.println("Неизвестный тип животного: " + animalType);
                List<String> availableTypes = ReflectionUtils.getAvailableAnimalTypes();
                System.out.println("Доступные типы: " + String.join(", ", availableTypes));
            }

        } else if ("-e".equals(type)) {
            if (parts.length < 3) {
                System.out.println("Использование: add -e <тип> <имя>");
                List<String> availableTypes = ReflectionUtils.getAvailableEmployeeTypes();
                System.out.println("Доступные типы: " + String.join(", ", availableTypes));
                return;
            }

            String[] employeeParts = parts[2].split("\\s+", 2);
            if (employeeParts.length < 2) {
                System.out.println("Использование: add -e <тип> <имя>");
                return;
            }

            String employeeType = employeeParts[0].toLowerCase();
            String name = employeeParts[1];

            Employee employee = ReflectionUtils.createEmployeeByType(employeeType, name);
            if (employee != null) {
                zooManager.addEmployee(employee);
            } else {
                System.out.println("Неизвестный тип сотрудника: " + employeeType);
                List<String> availableTypes = ReflectionUtils.getAvailableEmployeeTypes();
                System.out.println("Доступные типы: " + String.join(", ", availableTypes));
            }

        } else {
            System.out.println("Использование: add <-a|-e> <тип> <имя>");
        }
    }


    /**
     * Обработка команды stat - статистика по животным.
     */
    private static void handleStatCommand() {
        List<Animal> animals = zooManager.getAnimals();

        if (animals.isEmpty()) {
            System.out.println("В зоопарке пока нет животных.");
            return;
        }
        System.out.println("\n=== Статистика зоопарка ===");
        System.out.println();
        System.out.println(reportService.buildShortSummary(zooManager.getAnimals()));
        System.out.println();
    }

    /**
     * Обработка команды sched - расписание ухода за животными.
     */
    private static void handleSchedCommand() {
        List<Animal> animals = zooManager.getAnimals();
        
        if (animals.isEmpty()) {
            System.out.println("В зоопарке пока нет животных.");
            return;
        }

        System.out.println("\n=== Расписание ухода за животными на сегодня ===");
        System.out.println();
        System.out.println(reportService.buildFeedingSchedule(animals));
        System.out.println(reportService.buildMedicalSchedule(animals));
        System.out.println(reportService.buildCleaningSchedule(animals));
    }

    /**
     * Обработка команды show - информация о животных конкретного вида.
     */
    private static void handleShowCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Использование: show <вид>");
            System.out.println("Пример: show Wolf");
            return;
        }

        String species = parts[1];
        List<Animal> animals = zooManager.getAnimalsBySpecies(species);

        if (animals.isEmpty()) {
            System.out.println("Животных вида '" + species + "' не найдено в зоопарке.");
            return;
        }

        System.out.println("\n=== Информация о животных вида: " + species + " ===");
        System.out.println("Количество: " + animals.size());
        System.out.println("\nСписок:");
        for (Animal animal : animals) {
            System.out.println("  - " + animal.getName() + " (" + animal.getSpecies() + ")");
            System.out.print("    Звук: ");
            animal.makeSound();
            System.out.print("    Действие: ");
            animal.performDailyActivity();
        }
        System.out.println();
    }

    /**
     * Обработка команды employees/emps - информация о сотрудниках.
     */
    private static void handleEmployeesCommand() {
        List<Employee> employees = zooManager.getEmployees();
        System.out.println("\n" + reportService.buildEmployeesInfo(employees));
    }

    /**
     * Обработка команды info - список доступных команд.
     */
    private static void handleInfoCommand() {
        List<String> animalTypes = ReflectionUtils.getAvailableAnimalTypes();
        List<String> employeeTypes = ReflectionUtils.getAvailableEmployeeTypes();
        
        System.out.println("\n=== Доступные команды ===");
        System.out.println("add -a <тип> <имя>     - Добавить животное");
        System.out.println("                            Типы: " + String.join(", ", animalTypes));
        System.out.println();
        System.out.println("add -e <тип> <имя>    - Добавить сотрудника");
        System.out.println("                            Типы: " + String.join(", ", employeeTypes));
        System.out.println();
        System.out.println("stat                       - Показать статистику по животным");
        System.out.println("                            (общее количество и по видам)");
        System.out.println();
        System.out.println("sched                      - Показать расписание ухода за животными");
        System.out.println();
        System.out.println("show <вид>                 - Показать информацию о животных конкретного вида");
        System.out.println("                            Пример: show Wolf");
        System.out.println();
        System.out.println("employees / emps           - Показать информацию о сотрудниках зоопарка");
        System.out.println();
        System.out.println("info                       - Показать этот список команд");
        System.out.println();
        System.out.println("exit / quit                - Выход из программы");
        System.out.println();
    }
}
