package org.example.care;

import org.example.animals.Animal;

/**
 * ISP: интерфейс для уборки вольера.
 */
public interface Cleanable {
    void clean(Animal animal);
}


