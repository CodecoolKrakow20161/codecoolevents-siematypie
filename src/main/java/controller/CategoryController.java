package controller;

import dao.CategoryDaoPostgres;
import dao.Dao;

import java.util.Map;

public class CategoryController {
    private static Dao dao = new CategoryDaoPostgres();

    static Map<String,Object> renderCategories(Map<String,Object> params) {
        //Get events from database by Dao
        params.put("categoryContainer", dao.getAll());
        return params;
    }
}
