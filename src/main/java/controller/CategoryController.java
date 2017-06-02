package controller;

import dao.CategoryDao;
import dao.CategoryDaoPostgres;
import models.Category;
import utils.JsonTransformer;


import java.util.HashMap;
import java.util.Map;

public class CategoryController {
    private static CategoryDao dao = new CategoryDaoPostgres();
    private static JsonTransformer jsonTransformer = new JsonTransformer();

    public static Map<String,Object> getCategories() {
        //Get events from database by Dao
        HashMap<String, Object> catMap = new HashMap<>();
        catMap.put("categoryContainer", dao.getAll());
        return catMap;
    }

    public static String addCategory(String name){
        Category cat = new Category(name);
        return jsonTransformer.render(dao.addAndReturn(cat));
    }

    public static void deleteCategory(Integer catId){
        if (catId == 1){
            throw new IllegalArgumentException("You can't delete general category!");
        } else {
            dao.delete(catId);
        }
    }
}
