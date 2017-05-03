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

    public static ModelAndView renderProducts(Request req, Response res) {
        //Get events from database by Dao
        Map params = new HashMap<>();
        params.put("eventContainer", dao.getAll());
        return new ModelAndView(params, "product/index");
    }
}
