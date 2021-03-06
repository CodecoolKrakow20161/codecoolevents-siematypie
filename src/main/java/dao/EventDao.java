package dao;

import models.Event;

import java.util.List;

public interface EventDao{
     void addOrUpdate(Event event);
     void delete(Integer id);
     Event getById(Integer id);
     List<Event> getAll();
     List<Event> findByName(String searchPhrase);
     List<Event> getFiltered(List<Integer> catIds);
}
