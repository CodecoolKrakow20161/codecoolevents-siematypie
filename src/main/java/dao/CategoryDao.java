package dao;

import models.Category;

import java.util.List;

public interface CategoryDao{
     void addOrUpdate(Category category);
     Category getById(Integer id);
     List<Category> getAll();
}
