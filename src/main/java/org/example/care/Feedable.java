package org.example.care;

import org.example.animals.Animal;

/**
 * ISP: интерфейс для сущностей, которые умеют кормить животных.
 */
public interface Feedable {
    void feed(Animal animal);
}


