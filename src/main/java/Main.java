import controller.EventController;
import controller.ParamsMap;

import dao.PostgressConnectionHelper;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import utils.JsonTransformer;
import utils.ResponseError;

import  static spark.Spark.*;


public class Main {

    public static void main(String[] args) {
        PostgressConnectionHelper.setDbCon("localhost:5432","event",
                                            "event_owner", "secretpass");
        
        staticFileLocation("/public");
        port(8888);

        get("/event/all", "application/json", (req, res) -> EventController.getAllEventsJson());

        // Always start with more specific routes
        get("/event/find/:searchPhrase", "application/json", (req, res) -> {
            String searchPhrase = req.params(":searchPhrase");
            return EventController.findAndReturnJson(searchPhrase);
        });


        get("/event/:id", "application/json", (req, res) -> {
            String stringId = req.params(":id");
            Integer id = Integer.parseInt(stringId);
            return EventController.getEventJson(id);
        });


        // Always add generic routes to the end
//        get("/", AppController::renderIndex, new ThymeleafTemplateEngine());

        // Equivalent with above
        get("/", (Request req, Response res) -> {
            ParamsMap p = new ParamsMap();
            p.putAllCategories();
            p.putAllEvents();
            return new ThymeleafTemplateEngine().render( p.getModelAndView("product/index") );
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(new JsonTransformer().render(new ResponseError(e)));
        });

    }


}