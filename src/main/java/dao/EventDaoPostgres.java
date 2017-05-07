package dao;

import models.Category;
import models.Event;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.data.Row;
import org.sql2o.data.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDaoPostgres implements EventDao {
    private Sql2o sql2o;
    private CategoryDao catDao = new CategoryDaoPostgres();

    public EventDaoPostgres() {
        this.sql2o = PostgressConnectionHelper.getDb();
    }

    public void addOrUpdate(Event event){
        String insert = "insert into events( name, date, description, categoryId) values (:name, :date, :desc, :catId)";
        String update = "update events set name = :name, date = :date, description=:desc, categoryId=:catId where id=:id";

        Integer eventId = event.getId();
        Query query = null;
        try (Connection con = sql2o.open()) {
            if (eventId != null){
                query = con.createQuery(update);
                query.addParameter("id", eventId);
            } else {
                query = con.createQuery(insert);
            }
            query.addParameter("name", event.getName())
            .addParameter("date", event.getRawDate())
            .addParameter("desc", event.getDescription())
            .addParameter("catId", event.getCategory().getId());
            query.executeUpdate();
        }
    }

    @Override
    public Event getById(Integer id) {
        String query = "select * from events where id = :id";
        Table t;
        try (Connection con = sql2o.open()) {
            t = con.createQuery(query).addParameter("id", id).executeAndFetchTable();
        }
        List<Event> eventList = getEventsFromTable(t);
        return eventList.isEmpty() ? null : eventList.get(0);
    }

    @Override
    public List<Event> getAll() {
        String query = "select * from events";
        Table t;
        try (Connection con = sql2o.open()) {
            t = con.createQuery(query).executeAndFetchTable();
        }
        return getEventsFromTable(t);
    }

    public List<Event> findByName(String searchPhrase){
        searchPhrase = searchPhrase.replace("'", "''");
        String query = "select * from events where lower(name) like lower('";
        if (searchPhrase.length() < 3){
            query = query + searchPhrase ;
        } else {
            query = query + "%" + searchPhrase;
        }
        query = query + "%')";
        System.out.println(query);

        Table t;
        try (Connection con = sql2o.open()) {
            t = con.createQuery(query).executeAndFetchTable();
        }
        return getEventsFromTable(t);
    }

    @Override
    public List<Event> getFiltered(List<Integer> catIds) {
        String str = catIds.toString();
        String query = "select * from events where categoryId in (" + str.substring(1,str.length() -1) + ")";
        Table t;
        try (Connection con = sql2o.open()) {
            t = con.createQuery(query).executeAndFetchTable();
        }
        return getEventsFromTable(t);

    }

    private List<Event> getEventsFromTable(Table t){
        List<Event> eventList = new ArrayList<>();
        for (Row r : t.rows()) {
            System.out.println(r);
            int id = r.getInteger("id");
            String name = r.getString("name");
            Date date = r.getDate("date");
            String description = r.getString("description");
            Category category = catDao.getById(r.getInteger("categoryId"));
            eventList.add(new Event(id, name, date, description, category));
        }
        return eventList;
    }

}
