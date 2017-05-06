package dao;

import models.Category;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;


import java.util.List;

public class CategoryDaoPostgres implements Dao<Category> {
    private Sql2o sql2o;

    public CategoryDaoPostgres() {
        this.sql2o = PostgressConnectionHelper.getDb();
    }

    @Override
    public void addOrUpdate(Category category) {
        String insert = "insert into categories(name) values (:name)";
        String update = "update categories set name = :name where id=:id";

        Integer categoryId = category.getId();
        Query query = null;
        try (Connection con = sql2o.open()) {
            if (categoryId != null){
                query = con.createQuery(update);
                query.addParameter("id", categoryId);
            } else {
                query = con.createQuery(insert);
            }
            query.addParameter("name", category.getName());
            query.executeUpdate();
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
