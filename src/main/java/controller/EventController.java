package controller;

import dao.Dao;
import dao.EventDaoPostgres;
import models.Event;

import java.util.HashMap;
import java.util.Map;

public class EventController {
    private static Dao<Event> dao = new EventDaoPostgres();

     static Map<String,Object> getEvents() {
        //Get events from database by Dao
         HashMap<String, Object> eventMap = new HashMap<>();
         eventMap.put("eventContainer", dao.getAll());
         return eventMap;
    }
}
