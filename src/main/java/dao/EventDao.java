package dao;

import models.Event;
import java.util.List;

public interface EventDao {
    public Event getEventById(Integer id);
    public List<Event> getAllEvents();
}
