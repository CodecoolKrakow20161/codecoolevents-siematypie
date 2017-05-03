package dao;

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

    public EventDaoPostgres() {
        this.sql2o = PostgressConnectionHelper.getDb();
    }

    public void addOrUpdateEvent(Event event){
        String insert = "insert into events( name, date, description, category) values (:name, :date, :desc, :cat)";
        String update = "update events set name = :name, date = :date, description=:desc, category=:cat where id=:id";

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
            .addParameter("cat", event.getCategory());
            query.executeUpdate();
        }
    }

    @Override
    public Event getEventById(Integer id) {
        String query = "select * from events where id = :id";
        Table t;
        try (Connection con = sql2o.open()) {
            t = con.createQuery(query).addParameter("id", id).executeAndFetchTable();
        }
        List<Event> eventList = getEventsFromTable(t);
        return eventList.isEmpty() ? null : eventList.get(0);
    }

    @Override
    public List<Event> getAllEvents() {
        Table t;
        try (Connection con = sql2o.open()) {
            t = con.createQuery("select * from events").executeAndFetchTable();
        }
        return getEventsFromTable(t);
    }

    private List<Event> getEventsFromTable(Table t){
        List<Event> eventList = new ArrayList<>();
        for (Row r : t.rows()) {
            int id = r.getInteger("id");
            String name = r.getString("name");
            Date date = r.getDate("date");
            String description = r.getString("description");
            String category = r.getString("category");
            eventList.add(new Event(id, name, date, description, category));
        }
        return eventList;
    }
}
