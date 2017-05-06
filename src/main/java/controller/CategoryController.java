package controller;

import dao.CategoryDaoPostgres;
import dao.Dao;

import java.util.HashMap;
import java.util.Map;

public class CategoryController {
    private static Dao dao = new CategoryDaoPostgres();

    static Map<String,Object> getCategories() {
        //Get events from database by Dao
        HashMap<String, Object> catMap = new HashMap<>();
        catMap.put("categoryContainer", dao.getAll());
        return catMap;
    }
}
