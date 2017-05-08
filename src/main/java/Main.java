import controller.CategoryController;
import controller.EventController;
import controller.ParamsMap;

import dao.CategoryDao;
import dao.CategoryDaoPostgres;
import dao.PostgressConnectionHelper;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import utils.JsonTransformer;
import utils.ResponseError;
import models.Category;

import java.util.List;

import  static spark.Spark.*;


public class Main {

    public static void main(String[] args) {
        PostgressConnectionHelper.setDbCon("localhost:5432","event",
                                            "event_owner", "secretpass");
        
        staticFileLocation("/public");
        port(8888);

        post("/event/add","application/json", ((req, res) -> {
            String name = req.queryParams("name");
            String date = req.queryParams("date");
            String desc = req.queryParams("desc");
            Integer catId = Integer.parseInt(req.queryParams("catId"));
            Category category = new CategoryDaoPostgres().getById(catId);

            if (category == null){
                throw new IllegalArgumentException("Category doesn't exist");
            }
            EventController.addEvent(name, date, desc, category);
            return "Event " + name + "successfully added!";
        }));


        post("/category/add","application/json", ((req, res) ->
                CategoryController.addCategory(req.queryParams("name"))));

        post("/event/filter", ((req, res) -> {
            List<Integer> lst = new JsonTransformer().parseToList(req.body(), Integer.class);
            return EventController.getFilteredEvents(lst);
        }));

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

        delete("/event/:id", "application/json", (req, res) -> {
            String stringId = req.params(":id");
            Integer id = Integer.parseInt(stringId);
            EventController.deleteEvent(id);
            return "Event successfully deleted";
        });



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