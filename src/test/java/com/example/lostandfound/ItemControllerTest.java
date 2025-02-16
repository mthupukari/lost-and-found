package com.example.lostandfound;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.example.lostandfound.model.Item;
import com.example.lostandfound.model.User;
import com.example.lostandfound.repository.ItemRepository;
import com.example.lostandfound.repository.UserRepository;
import com.example.lostandfound.security.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User userA;
    private User userB;
    private Item item;

    @BeforeEach
    public void setup() {
        //clean up 
        itemRepository.deleteAll();
        userRepository.deleteAll();

        //create two users
        userA = new User("userA", passwordEncoder.encode("passwordA"), "userA@example.com", "ROLE_USER");
        userB = new User("userB", passwordEncoder.encode("passwordB"), "userB@example.com", "ROLE_USER");
        userRepository.save(userA);
        userRepository.save(userB);

        //create item for user A
        item = new Item();
        item.setTitle("Lost Keys");
        item.setDescription("A set of keys lost at the library");
        item.setLocation("Library");
        item.setDate(LocalDate.now());
        item.setType("lost");
        item.setImageUrl("");
        item.setUser(userA);
        itemRepository.save(item);
    }

    @Test
    public void testDeleteItemAuthorized() throws Exception {
        // generate JWT token for userA (owner of item)
        String token = jwtUtil.generateToken(userA.getUsername());

        // Perform DELETE request on /api/items/{id} with userA's token and expect 200 OK
        mockMvc.perform(delete("/api/items/" + item.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteItemUnauthorized() throws Exception {
        // Generate JWT token for userB (not the owner)
        String token = jwtUtil.generateToken(userB.getUsername());

        // Attempt to delete the item (owned by userA) using userB's token and expect 403 Forbidden
        mockMvc.perform(delete("/api/items/" + item.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
