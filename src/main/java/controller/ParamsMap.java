package controller;

import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

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

    public String renderTemplate(String viewName){
        return new ThymeleafTemplateEngine().render(this.getModelAndView(viewName) );
    }
}
