import controller.CategoryController;
import controller.EventController;
import controller.ParamsMap;

import dao.CategoryDaoPostgres;
import dao.PostgressConnectionHelper;
import io.jsonwebtoken.JwtException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import spark.Request;
import spark.Response;
import utils.JWTGenerator;
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
        Integer tokenExpirationTime = 10800000;

        before("protected/*", (request, response) -> {
            try {
                JWTGenerator.getInstance().parseJWT(request.headers("x-admin-token"));
            } catch (JwtException e) {
                e.printStackTrace();
                String msg = "Your session is not valid, please login again to get access to admin features";
                throw halt(401, new JsonTransformer().render(new ResponseError(msg)));
            }
        });

        post("protected/event/add","application/json", ((req, res) -> {
            String name = Jsoup.clean(req.queryParams("name"), Whitelist.none());
            String date = Jsoup.clean(req.queryParams("date"), Whitelist.none());
            String desc = Jsoup.clean(req.queryParams("desc"), Whitelist.basic());
            Integer catId = Integer.parseInt(req.queryParams("catId"));
            Category category = new CategoryDaoPostgres().getById(catId);

            if (category == null){
                throw new IllegalArgumentException("Category doesn't exist");
            }
            EventController.addEvent(name, date, desc, category);
            return "Event " + name + "successfully added!";
        }));


        post("protected/category/add","application/json", ((req, res) ->
                CategoryController.addCategory(Jsoup.clean(req.queryParams("name"), Whitelist.none()))));

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

        delete("protected/event/:id", "application/json", (req, res) -> {
            String stringId = req.params(":id");
            Integer id = Integer.parseInt(stringId);
            EventController.deleteEvent(id);
            return "Event successfully deleted";
        });

        put("protected/event/:id", "application/json", (req, res) -> {
            String stringId = req.params(":id");
            Integer id = Integer.parseInt(stringId);
            String name = Jsoup.clean(req.queryParams("name"), Whitelist.none());
            String date = Jsoup.clean(req.queryParams("date"), Whitelist.none());
            String desc = Jsoup.clean(req.queryParams("desc"), Whitelist.basic());
            Integer catId = Integer.parseInt(req.queryParams("catId"));
            Category category = new CategoryDaoPostgres().getById(catId);

            if (category == null){
                throw new IllegalArgumentException("Category doesn't exist");
            }
            EventController.updateEvent(id, name, date, desc, category);
            return "Event successfully updated";
        });

        post("/login", ((req, res) -> {
            System.out.println(req.queryParams("name"));
            if ((!(req.queryParams("name").equals("admin")) ||
                    !req.queryParams("password").equals("codecool"))) {
                throw halt(401, new JsonTransformer().render(new ResponseError("Invalid credentials")));
            }

            String jwt = JWTGenerator.getInstance().createJWT("admin", tokenExpirationTime);

            res.header("x-admin-token", jwt);
            return "{\"expirationTime\":"+ tokenExpirationTime + "}";
        }));

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


}