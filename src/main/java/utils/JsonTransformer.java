package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import spark.ResponseTransformer;

import java.util.List;

public class JsonTransformer implements ResponseTransformer {
    private Gson gson = new GsonBuilder()
            .setDateFormat("dd-MM-yyyy").create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

    public JsonObject getJsonObject(String line){
        return gson.fromJson(line, JsonObject.class);
    }

    public <T> List<T> parseToList(String obj, Class<T> classe){
        return  gson.fromJson(obj, new TypeToken<List<T>>(){}.getType());
    }
}