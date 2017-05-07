package dao;

import models.Category;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;


import java.util.List;
import java.util.Locale;

public class CategoryDaoPostgres implements CategoryDao {
    private Sql2o sql2o;

    public CategoryDaoPostgres() {
        this.sql2o = PostgressConnectionHelper.getDb();
    }

    @Override
    public void addOrUpdate(Category category) {
        String insert = "insert into categories(name) values (:name)";
        String update = "update categories set name = :name where id=:id";

        Integer categoryId = category.getId();
        Query query;
        try (Connection con = sql2o.open()) {
            if (categoryId != null){
                query = con.createQuery(update);
                query.addParameter("id", categoryId);
            } else {
                query = con.createQuery(insert);
            }
            query.addParameter("name", category.getName());
            query.executeUpdate();
        } catch (Sql2oException e){
            throw new IllegalArgumentException("Category name '" + category.getName() + "' id already taken");
        }
    }

    public Category addAndReturn(Category category){
        String insert = "insert into categories(name) values (:name) RETURNING name, id;";
        try(Connection con = sql2o.open()) {
            return con.createQuery(insert).addParameter("name", category.getName()).executeAndFetchFirst(Category.class);
        }  catch (Sql2oException e){
            throw new IllegalArgumentException("Category name '" + category.getName() + "' id already taken");
        }
    }

    @Override
    public Category getById(Integer id) {
        String query = "select * from categories where id = " + id;

        try(Connection con = sql2o.open()) {
           return con.createQuery(query).executeAndFetchFirst(Category.class);
        }
    }

    @Override
    public List<Category> getAll() {
        String query = "select * from categories";
        try(Connection con = sql2o.open()) {
            return con.createQuery(query).executeAndFetch(Category.class);
        }
    }

    public Boolean nameExists(String name){
        String query = "select exists(select 1 from categories where name = :name )";
        try (Connection con = sql2o.open()) {
            return con.createQuery(query).addParameter("name", name).executeScalar(Boolean.class);
        }
    }
}
