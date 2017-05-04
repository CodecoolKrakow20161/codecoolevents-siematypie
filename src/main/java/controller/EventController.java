package controller;

import dao.Dao;
import dao.EventDaoPostgres;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class EventController {
    private static Dao dao = new EventDaoPostgres();

     static Map<String,Object> renderEvents(Map<String,Object> params) {
        //Get events from database by Dao
        params.put("eventContainer", dao.getAll());
        return params;
    }
}
