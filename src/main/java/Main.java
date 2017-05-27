import controller.CategoryController;
import controller.EventController;
import controller.ParamsMap;

import dao.CategoryDaoPostgres;
import dao.PostgressConnectionHelper;
import spark.Request;
import spark.Response;
import utils.JsonTransformer;
import utils.ResponseError;
import models.Category;
import java.util.List;

import  static spark.Spark.*;


public class Main {

    public static void main(String[] args) {
        PostgressConnectionHelper.setDbCon("ec2-54-247-99-159.eu-west-1.compute.amazonaws.com:5432",
                "dc0sbu4g2pr6du", "tmbznnxxkubvph",
                "a013fa793e1d02b3c729e054657b7898674b65b58c33acb47ad0591b4f4565c4");
        
        staticFileLocation("/public");
        port(getHerokuAssignedPort());

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

        put("/event/:id", "application/json", (req, res) -> {
            String stringId = req.params(":id");
            Integer id = Integer.parseInt(stringId);
            String name = req.queryParams("name");
            String date = req.queryParams("date");
            String desc = req.queryParams("desc");
            Integer catId = Integer.parseInt(req.queryParams("catId"));
            Category category = new CategoryDaoPostgres().getById(catId);

            if (category == null){
                throw new IllegalArgumentException("Category doesn't exist");
            }
            EventController.updateEvent(id, name, date, desc, category);
            return "Event successfully updated";
        });


        // Equivalent with above
        get("/", (Request req, Response res) -> {
            ParamsMap p = new ParamsMap();
            p.putAllCategories();
            p.putAllEvents();
            return p.renderTemplate("product/index");
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(new JsonTransformer().render(new ResponseError(e)));
        });

    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 8888; //return default port if heroku-port isn't set (i.e. on localhost)
    }


}