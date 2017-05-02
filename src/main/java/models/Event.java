package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Integer id;
    private String name;
    private String description;
    private String category;
    private Date date;

    public Event(String name, Date date, String description, String category) {
        this(name, description, category);
        this.date= date;
    }

    public Event(String name, String stringDate, String description, String category){
        this(name, description, category);
        try {
            this.date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            this.date = null;
        }
    }

    public Event(int id, String name, String stringDate, String description, String category){
        this(name, stringDate, description, category);
        this.id = id;
    }

    public Event(int id, String name, Date Date, String description, String category){
        this(name, Date, description, category);
        this.id = id;
    }


    private Event(String name, String description, String category){
        this.description = description;
        this.category = category;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
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
