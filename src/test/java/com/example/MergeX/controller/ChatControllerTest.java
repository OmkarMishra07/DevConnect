package com.example.MergeX.controller;

import com.example.MergeX.Dto.CreateMessageDto;
import com.example.MergeX.service.ChatService;
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
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @Test
    @DisplayName("TC-09: Send Friend Request - Request sent")
    public void testSendFriendRequest() throws Exception {
        Long friendId = 2L;

        mockMvc.perform(post("/chat/friend-request/" + friendId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("TC-09: Send Friend Request -> Passed (Request sent)");
    }

    @Test
    @DisplayName("TC-10: Accept Friend Request - Request accepted")
    public void testAcceptFriendRequest() throws Exception {
        Long friendshipId = 1L;

        mockMvc.perform(post("/chat/friend-request/" + friendshipId + "/accept")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        System.out.println("TC-10: Accept Friend Request -> Passed (Request accepted)");
    }

    @Test
    @DisplayName("TC-11: Send Group Message - Message sent")
    public void testSendMessage() throws Exception {
        Long groupId = 1L;
        CreateMessageDto messageDto = new CreateMessageDto();
        messageDto.setContent("Hello team!");

        mockMvc.perform(post("/chat/groups/" + groupId + "/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk());

        System.out.println("TC-11: Send Group Message -> Passed (Message sent)");
    }
}
