package com.example.lostandfound.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String description;
    private String location;
    private LocalDate date;
    private String type;
    private String imageUrl;

    public Item() {
    }
    
    public Item(String title, String descripton, String location, LocalDate date, String type, String imageUrl) {
        this.title = title;
        this.description = descripton;
        this.location = location;
        this.date = date;
        this.type = type;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long updateItem(Item updatedItem) {
        this.setTitle(updatedItem.getTitle());
        this.setDescription(updatedItem.getDescription());
        this.setLocation(updatedItem.getLocation());
        this.setDate(updatedItem.getDate());
        this.setType(updatedItem.getType());
        this.setImageUrl(updatedItem.getImageUrl());

        return this.id;
    }
}
