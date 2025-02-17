package com.example.lostandfound.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.lostandfound.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
       "WHERE (:title IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
       "AND (:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
       "AND (:type IS NULL OR i.type = :type) " +
       "AND (:date IS NULL OR i.date = :date)")
    List<Item> searchItems(@Param("title") String title, 
                       @Param("location") String location, 
                       @Param("type") String type, 
                       @Param("date") LocalDate date);

}
