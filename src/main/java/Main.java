import controller.EventController;
import controller.ParamsMap;

import dao.PostgressConnectionHelper;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import  static spark.Spark.*;


public class Main {

    public static void main(String[] args) {
        PostgressConnectionHelper.setDbCon("localhost:5432","event",
                                            "event_owner", "secretpass");
//        new EventDaoPostgres().addOrUpdateEvent(new Event("beka", "11-04-1993", "desc", "cat"));
        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFileLocation("/public");
        port(8888);

        // Always start with more specific routes
        get("/hello", (req, res) -> "Hello World");

        // Always add generic routes to the end
//        get("/", AppController::renderIndex, new ThymeleafTemplateEngine());

        // Equivalent with above
        get("/", (Request req, Response res) -> {
            ParamsMap p = new ParamsMap();
            p.putAllCategories();
            p.putAllEvents();
            return new ThymeleafTemplateEngine().render( p.getModelAndView("product/index") );
        });
    }


}