package models;

import java.util.Date;

public class Event {
    private String name;
    private String description;
    private String category;
    private Date date;

    public Event(String name, Date date, String description, String category) {
        this.name = name;
        this.date= date;
        this.description = description;
        this.category = category;
    }
}
