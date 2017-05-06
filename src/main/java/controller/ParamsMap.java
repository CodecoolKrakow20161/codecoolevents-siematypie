package controller;

import spark.ModelAndView;

import java.util.HashMap;

public class ParamsMap extends HashMap<String, Object> {
    public void putAllEvents(){
        this.putAll(EventController.getEvents());
    }

    public void putAllCategories(){
        this.putAll(CategoryController.getCategories());
    }

    public ModelAndView getModelAndView(String viewName){
        return new ModelAndView(this, viewName);
    }
}
