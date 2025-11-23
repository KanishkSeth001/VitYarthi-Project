package edu.ccrm.service;

import java.util.List;

public interface Persistable<T> {
    void saveToFile(String filename) throws DataAccessException;
    List<T> loadFromFile(String filename) throws DataAccessException;
}