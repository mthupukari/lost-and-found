package com.example.lostandfound.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lostandfound.dto.ContactInfoDTO;
import com.example.lostandfound.dto.PublicItemDTO;
import com.example.lostandfound.model.Item;
import com.example.lostandfound.model.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.UserRepository;

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
    public ResponseEntity<Item> createItem(@RequestBody Item newItem) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Load the user from the database using their username
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isPresent()) {
            newItem.setUser(optionalUser.get());
        } else {
            // If the user is not found, return an error response or handle as needed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Item savedItem = itemRepository.save(newItem);
        return ResponseEntity.ok(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (!optionalItem.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        if (!item.getUser().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
            
        item.updateItem(updatedItem);
        itemRepository.save(item);
        return ResponseEntity.ok(item);
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        if (!item.getUser().getUsername().equals(currentUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        itemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PublicItemDTO>> searchItems(
        @RequestParam(required = false) String title, 
        @RequestParam(required = false) String location, 
        @RequestParam(required = false) String type, 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Item> items = itemRepository.searchItems(title, location, type, date);
        List<PublicItemDTO> publicItems = items.stream()
                .map(this::convertToPublicDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(publicItems);
    }
    
    @GetMapping("/{id}/contact")
    public ResponseEntity<ContactInfoDTO> getContactInfo(@PathVariable Long id) {
        // retrieve the item by ID
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Item item = optionalItem.get();
        ContactInfoDTO contactInfo = new ContactInfoDTO();
        contactInfo.setEmail(item.getUser().getEmail());

        return ResponseEntity.ok(contactInfo);
    }
    
    private PublicItemDTO convertToPublicDTO(Item item) {
        return new PublicItemDTO(
            item.getId(),
            item.getTitle(), 
            item.getDescription(), 
            item.getLocation(), 
            item.getDate(), 
            item.getType(), 
            item.getImageUrl()
        );
    }

}
