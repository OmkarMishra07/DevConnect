package com.example.MergeX.controller;

import com.example.MergeX.Dto.CompleteProfileRequestDto;
import com.example.MergeX.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("TC-06: Complete Profile - Profile updated successfully")
    public void testCompleteProfile() throws Exception {
        CompleteProfileRequestDto request = new CompleteProfileRequestDto();
        request.setBio("Test Bio");
        request.setSkills(java.util.Set.of("Java", "React"));

        mockMvc.perform(post("/users/complete-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        System.out.println("TC-06: Complete Profile -> Passed (Profile updated successfully)");
    }

    @Test
    @DisplayName("TC-07: Get User by ID - User details retrieved")
    public void testGetUserById() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/getuser/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("TC-07: Get User by ID -> Passed (User details retrieved)");
    }

    @Test
    @DisplayName("TC-08: Search Users by Skill - User list returned")
    public void testSearchUsersBySkill() throws Exception {
        String skill = "Java";

        mockMvc.perform(get("/users/search")
                .param("skill", skill)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("TC-08: Search Users by Skill -> Passed (User list returned)");
    }
}
