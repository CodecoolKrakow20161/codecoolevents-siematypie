package dao;

import java.util.List;

public interface Dao<T>{
     void addOrUpdate(T object);
     T getById(Integer id);
     List<T> getAll();
}
