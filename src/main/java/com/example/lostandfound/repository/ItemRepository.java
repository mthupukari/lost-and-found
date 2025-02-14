package com.example.lostandfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.lostandfound.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {}
