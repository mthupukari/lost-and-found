package com.example.lostandfound.controller;

import com.example.lostandfound.model.Item;
import com.example.lostandfound.model.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.UserRepository;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isPresent()) {
            return ResponseEntity.ok(item.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Item createItem(@RequestBody Item newItem) {
        // using 1L for testing purposes
        // ensure that a user had been made for this method to work
        Optional<User> testUser = userRepository.findById(1L);

        if (testUser.isPresent()) {
            newItem.setUser(testUser.get());
        }

        return itemRepository.save(newItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.updateItem(updatedItem);
            itemRepository.save(item);
            return ResponseEntity.ok(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
