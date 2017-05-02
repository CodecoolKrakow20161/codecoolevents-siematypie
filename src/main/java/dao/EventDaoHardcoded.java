package dao;

import models.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDaoHardcoded implements EventDao{

    @Override
    public Event getEventById(Integer id) {
        return null;
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(new Event(10,"HEHE", new Date(), "BLA", "BEKA"));
        eventList.add(new Event(4,"HEHasdE", new Date(), "BasdLA", "BasdEKA"));
        eventList.add(new Event(1,"HEASHE", new Date(), "BLA", "BEKsdxcA"));
        return eventList;
    }
}
