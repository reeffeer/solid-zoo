package org.example.care;

import org.example.animals.Animal;

/**
 * ISP: интерфейс для медицинского осмотра и лечения животных.
 */
public interface Treatable {
    void treat(Animal animal);
}


