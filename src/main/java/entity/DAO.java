package entity;

import java.util.List;
import java.util.Optional;

public interface DAO<T>
{
    Optional<T> get(int id);
    List<T> getAll();
    void insert(T item);
    void update(T item);
    void delete(T item);
    List<String> getColumnNames();
}