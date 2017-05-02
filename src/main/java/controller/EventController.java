package controller;

import dao.EventDao;
import dao.EventDaoHardcoded;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class EventController {
    private static EventDao eventDao = new EventDaoHardcoded();

    public static ModelAndView renderProducts(Request req, Response res) {
        //Get events from database by Dao
        Map params = new HashMap<>();
        params.put("eventContainer", eventDao.getAllEvents());
        return new ModelAndView(params, "product/index");
    }
}
