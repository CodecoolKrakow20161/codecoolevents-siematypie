package controller;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class AppController {
    public static ModelAndView renderIndex(Request req, Response res){
        Map<String, Object> params = new HashMap<>();
        params = EventController.renderEvents(params);
        params = CategoryController.renderCategories(params);
        return new ModelAndView(params, "product/index");
    }
}
