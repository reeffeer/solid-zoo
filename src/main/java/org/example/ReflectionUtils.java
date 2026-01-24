package org.example;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.animals.Animal;
import org.example.employees.Employee;

/**
 * Утилитный класс для работы с рефлексией.
 * Используется для динамического обнаружения классов животных и сотрудников.
 */
public class ReflectionUtils {

    private static final String ANIMALS_PACKAGE = "org.example.animals";
    private static final String EMPLOYEES_PACKAGE = "org.example.employees";

    /**
     * Находит все классы животных в пакете org.example.animals.
     * Возвращает список простых имен классов (без пакета).
     */
    public static List<String> getAvailableAnimalTypes() {
        return getClassesInPackage(ANIMALS_PACKAGE, Animal.class)
                .stream()
                .map(Class::getSimpleName)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Находит все классы сотрудников в пакете org.example.employees.
     * Возвращает список простых имен классов (без пакета).
     */
    public static List<String> getAvailableEmployeeTypes() {
        return getClassesInPackage(EMPLOYEES_PACKAGE, Employee.class)
                .stream()
                .map(Class::getSimpleName)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Создает животное по имени класса и имени животного.
     * 
     * @param typeName имя класса (например, "wolf", "parrot")
     * @param animalName имя животного
     * @return созданное животное или null, если класс не найден
     */
    public static Animal createAnimalByType(String typeName, String animalName) {
        try {
            Class<?> animalClass = findClassInPackage(ANIMALS_PACKAGE, typeName, Animal.class);
            if (animalClass == null) {
                return null;
            }

            Constructor<?> constructor = animalClass.getConstructor(String.class);
            return (Animal) constructor.newInstance(animalName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Создает сотрудника по имени класса и имени сотрудника.
     * 
     * @param typeName имя класса (например, "keeper", "vet")
     * @param employeeName имя сотрудника
     * @return созданный сотрудник или null, если класс не найден
     */
    public static Employee createEmployeeByType(String typeName, String employeeName) {
        try {
            String className = normalizeEmployeeTypeName(typeName);
            
            Class<?> employeeClass = findClassInPackage(EMPLOYEES_PACKAGE, className, Employee.class);
            if (employeeClass == null) {
                return null;
            }

            Constructor<?> constructor = employeeClass.getConstructor(String.class);
            return (Employee) constructor.newInstance(employeeName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Нормализует имя типа сотрудника (поддержка алиасов).
     * Возвращает правильное имя класса с корректным регистром.
     */
    private static String normalizeEmployeeTypeName(String typeName) {
        String lower = typeName.toLowerCase();
        if ("keeper".equals(lower) || "zookeeper".equals(lower)) {
            return "ZooKeeper";
        }
        if ("vet".equals(lower) || "veterinarian".equals(lower)) {
            return "Vet";
        }
        return typeName;
    }

    /**
     * Находит класс в указанном пакете по имени.
     * Пробует разные варианты написания имени класса.
     */
    private static Class<?> findClassInPackage(String packageName, String className, Class<?> baseClass) {
        String[] variants = {
            className,
        };
        
        if (className.equals(className.toLowerCase())) {
            variants = new String[]{
                className,
                capitalize(className),
            };
        }
        
        for (String variant : variants) {
            try {
                String fullClassName = packageName + "." + variant;
                Class<?> clazz = Class.forName(fullClassName);
                if (baseClass.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.equals(baseClass)) {
                    return clazz;
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                // Пробуем следующий вариант имени класса
            }
        }
        
        return null;
    }

    /**
     * Находит все классы в указанном пакете, которые наследуются от baseClass.
     */
    private static List<Class<?>> getClassesInPackage(String packageName, Class<?> baseClass) {
        List<Class<?>> classes = new ArrayList<>();
        
        try {
            java.net.URL resource = baseClass.getClassLoader().getResource(
                    packageName.replace('.', '/'));
            
            if (resource != null) {
                java.io.File directory = new java.io.File(resource.getFile());
                if (directory.exists()) {
                    String[] files = directory.list();
                    if (files != null) {
                        for (String file : files) {
                            if (file.endsWith(".class")) {
                                String className = file.substring(0, file.length() - 6);
                                try {
                                    Class<?> clazz = Class.forName(packageName + "." + className);
                                    if (baseClass.isAssignableFrom(clazz) 
                                            && !clazz.isInterface() 
                                            && !clazz.equals(baseClass)
                                            && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                                        classes.add(clazz);
                                    }
                                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                    // Игнорируем классы, которые не могут быть загружены
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return getKnownClasses(packageName, baseClass);
        }
        
        return classes.isEmpty() ? getKnownClasses(packageName, baseClass) : classes;
    }

    /**
     * Альтернативный метод: возвращает известные классы из пакета.
     * Используется как fallback, если сканирование файловой системы не работает.
     */
    private static List<Class<?>> getKnownClasses(String packageName, Class<?> baseClass) {
        List<Class<?>> classes = new ArrayList<>();
        
        if (ANIMALS_PACKAGE.equals(packageName) && Animal.class.equals(baseClass)) {
            String[] knownAnimals = {"Wolf", "Parrot", "Snake", "Monkey"};
            for (String animalName : knownAnimals) {
                try {
                    Class<?> clazz = Class.forName(packageName + "." + animalName);
                    if (baseClass.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Игнорируем классы, которые не найдены
                }
            }
        } else if (EMPLOYEES_PACKAGE.equals(packageName) && Employee.class.equals(baseClass)) {
            String[] knownEmployees = {"ZooKeeper", "Vet"};
            for (String employeeName : knownEmployees) {
                try {
                    Class<?> clazz = Class.forName(packageName + "." + employeeName);
                    if (baseClass.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Игнорируем классы, которые не найдены
                }
            }
        }
        
        return classes;
    }

    /**
     * Делает первую букву строки заглавной.
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}

