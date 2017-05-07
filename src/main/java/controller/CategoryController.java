package controller;

import dao.CategoryDao;
import dao.CategoryDaoPostgres;
import models.Category;


import java.util.HashMap;
import java.util.Map;

public class CategoryController {
    private static CategoryDao dao = new CategoryDaoPostgres();

    static Map<String,Object> getCategories() {
        //Get events from database by Dao
        HashMap<String, Object> catMap = new HashMap<>();
        catMap.put("categoryContainer", dao.getAll());
        return catMap;
    }
}
