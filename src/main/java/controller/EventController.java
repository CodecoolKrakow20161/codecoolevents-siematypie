package controller;

import dao.Dao;
import dao.EventDaoPostgres;
import models.Event;
import utils.JsonTransformer;

import java.util.HashMap;
import java.util.Map;

public class EventController {
    private static Dao<Event> dao = new EventDaoPostgres();
    private static JsonTransformer jsonTransformer = new JsonTransformer();

     static Map<String,Object> getEvents() {
        //Get events from database by Dao
         HashMap<String, Object> eventMap = new HashMap<>();
         eventMap.put("eventContainer", dao.getAll());
         return eventMap;
    }

    public static String getEventJson(int eventId){
        Event event = dao.getById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Requested event not found in the database");
        }
        return jsonTransformer.render(event);
    }
}
