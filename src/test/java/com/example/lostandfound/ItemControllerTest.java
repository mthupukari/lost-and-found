package com.example.lostandfound;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private Item item2; 
    private Item item3; 

    @BeforeEach
    public void setup() {
        // Clean up repositories
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Create two users
        userA = new User("userA", passwordEncoder.encode("passwordA"), "userA@example.com", "ROLE_USER");
        userB = new User("userB", passwordEncoder.encode("passwordB"), "userB@example.com", "ROLE_USER");
        userRepository.save(userA);
        userRepository.save(userB);

        // Create an item for userA (for delete and contact tests)
        item = new Item();
        item.setTitle("Lost Keys");
        item.setDescription("A set of keys lost at the library");
        item.setLocation("Library");
        item.setDate(LocalDate.now());
        item.setType("lost");
        item.setImageUrl("");
        item.setUser(userA);
        itemRepository.save(item);

        // Create additional items for search tests:
        // Item 2: "Lost Wallet" at "Campus", date = 2025-02-14, type "lost"
        item2 = new Item();
        item2.setTitle("Lost Wallet");
        item2.setDescription("A wallet lost on campus");
        item2.setLocation("Campus");
        item2.setDate(LocalDate.of(2025, 2, 14));
        item2.setType("lost");
        item2.setImageUrl("");
        item2.setUser(userA);
        itemRepository.save(item2);

        // Item 3: "Found Keys" at "Cafeteria", date = today, type "found"
        item3 = new Item();
        item3.setTitle("Found Keys");
        item3.setDescription("Keys found at the cafeteria");
        item3.setLocation("Cafeteria");
        item3.setDate(LocalDate.now());
        item3.setType("found");
        item3.setImageUrl("");
        item3.setUser(userB);
        itemRepository.save(item3);
    }

    @Test
    public void testDeleteItemAuthorized() throws Exception {
        String token = jwtUtil.generateToken(userA.getUsername());
        mockMvc.perform(delete("/api/items/" + item.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testDeleteItemUnauthorized() throws Exception {
        String token = jwtUtil.generateToken(userB.getUsername());
        mockMvc.perform(delete("/api/items/" + item.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testSearchByTitle() throws Exception {
        // Searching for items with title containing "Lost"
        // Expecting 2 items: "Lost Keys" and "Lost Wallet"
        mockMvc.perform(get("/api/items/search")
                .param("title", "Lost")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    
    @Test
    public void testSearchByLocation() throws Exception {
        // Searching for items with location "Library" should return 1 item ("Lost Keys")
        mockMvc.perform(get("/api/items/search")
                .param("location", "Library")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].location").value("Library"));
    }
    
    @Test
    public void testSearchByTypeAndDate() throws Exception {
        // Searching for lost items on the date 2025-02-14 should return 1 item ("Lost Wallet")
        mockMvc.perform(get("/api/items/search")
                .param("type", "lost")
                .param("date", "2025-02-14")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Lost Wallet"));
    }
    
    // Contact endpoint should be secured and return contact info only for authenticated users
    @Test
    public void testContactEndpointUnauthorized() throws Exception {
        // Attempt to access the contact endpoint without auth should return 403 Forbidden
        mockMvc.perform(get("/api/items/" + item.getId() + "/contact")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
    @Test
    public void testContactEndpointAuthorized() throws Exception {
        // Generate token for userA (owner of the item)
        String token = jwtUtil.generateToken(userA.getUsername());
        mockMvc.perform(get("/api/items/" + item.getId() + "/contact")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userA.getEmail()));
    }
}
