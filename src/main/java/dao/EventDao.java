package dao;

import models.Event;
import java.util.List;

public interface EventDao {
     void addOrUpdateEvent(Event event);
     Event getEventById(Integer id);
     List<Event> getAllEvents();
}
