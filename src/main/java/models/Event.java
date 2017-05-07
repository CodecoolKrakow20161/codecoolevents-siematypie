package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Integer id;
    private String name;
    private String description;
    private Category category;
    private Date date;

    public Event(String name, Date date, String description, Category category) {
        this(name, description, category);
        this.date= date;
    }

    public Event(String name, String stringDate, String description, Category category){
        this(name, description, category);
        try {
            this.date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            this.date = null;
        }
    }

    public Event(int id, String name, String stringDate, String description, Category category){
        this(name, stringDate, description, category);
        this.id = id;
    }

    public Event(int id, String name, Date Date, String description, Category category){
        this(name, Date, description, category);
        this.id = id;
    }


    private Event(String name, String description, Category category){
        validate(name, description);
        this.description = description;
        this.category = category;
        this.name = name;
    }

    private void validate(String name, String description){
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        if (description.replaceAll(" ", "").equals("") || description.isEmpty()){
            throw new IllegalArgumentException("Event description cannot be empty");
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public Date getRawDate() {
        return date;
    }

    public String getDate(){
        return dateFormat.format(date);
    }

    public Integer getId() {
        return id;
    }
}
