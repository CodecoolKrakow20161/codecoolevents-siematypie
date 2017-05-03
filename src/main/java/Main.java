import controller.EventController;
import dao.EventDao;
import dao.EventDaoPostgres;
import dao.PostgressConnectionHelper;
import models.Event;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        get("/", EventController::renderProducts, new ThymeleafTemplateEngine());
        // Equivalent with above
        get("/index", (Request req, Response res) -> {
            return new ThymeleafTemplateEngine().render( EventController.renderProducts(req, res) );
        });
    }


}