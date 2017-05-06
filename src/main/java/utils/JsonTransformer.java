package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    public Gson gson = new GsonBuilder()
            .setDateFormat("dd-MM-yyyy").create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

}