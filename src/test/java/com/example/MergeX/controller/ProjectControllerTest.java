package com.example.MergeX.controller;

import com.example.MergeX.Dto.ProjectDtoReq;
import com.example.MergeX.service.ProjectService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @Test
    @DisplayName("TC-03: Project Creation - Project created")
    public void testProjectCreation() throws Exception {
        ProjectDtoReq projectReq = new ProjectDtoReq();
        projectReq.setTitle("Test Project");
        projectReq.setDescription("A test project description");
        projectReq.setTagline("Test Tagline");
        projectReq.setTechStack(Set.of("Java", "Spring Boot"));

        mockMvc.perform(post("/projects/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectReq)))
                .andExpect(status().isCreated());

        System.out.println("TC-03: Project Creation -> Passed (Project created)");
    }

    @Test
    @DisplayName("TC-04: Join Project - User added to team")
    public void testJoinProject() throws Exception {
        Long projectId = 1L;

        mockMvc.perform(post("/projects/" + projectId + "/join")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("TC-04: Join Project -> Passed (User added to team)");
    }
}
