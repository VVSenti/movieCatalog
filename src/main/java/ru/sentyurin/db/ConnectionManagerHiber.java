package ru.sentyurin.db;

import org.hibernate.Session;

public interface ConnectionManagerHiber {
    Session openSession();
}
