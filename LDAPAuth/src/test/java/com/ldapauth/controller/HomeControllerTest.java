package com.ldapauth.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldapauth.HomeController;
import com.ldapauth.LdapService;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LdapService ldapService;

    
    @Test
    public void testLoginvalidate_ValidCredentials_ReturnsWelcomeMessage() throws Exception {
        // given - mock setup
        String username = "muthu";
        String password = "muthu";
        when(LdapService.authenticate(username, password)).thenReturn(true);

        // when - performing the request
        MvcResult mvcResult = mockMvc.perform(get("/loginvalidate")
                .param("username", username)
                .param("password", password))
                .andReturn();
        
        // then - verify method invocation
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(content).isNotNull(); // Assert content is not null
        Assertions.assertThat(content).isEqualTo("Welcome to the home page!"); // Assert content is as expected
    }
    

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(ldapService)).build(); // replace YourController with your controller class name
    }

    @Test
    public void testGetOU() throws Exception {
        // Mocking LDAPService behavior
        List<String> expectedOU = new ArrayList<>();
        expectedOU.add("");
       
        mockStatic(LdapService.class);
        when(LdapService.getOUFromCommonName("muthu")).thenReturn(expectedOU);

        // Performing the request
        MvcResult result = mockMvc.perform(get("/getOU")
                .param("cn", "muthu"))
                .andExpect(status().isOk())
                .andReturn();

        // Parsing and verifying the response
        String responseBody = result.getResponse().getContentAsString();
        List<String> actualOU = new ObjectMapper().readValue(responseBody, new TypeReference<List<String>>() {});

        assertEquals(expectedOU, actualOU);
     
        
    }
    public void testIndex_ValidCredentials() throws Exception {
        // Mock LDAP authentication to return true
        when(ldapService.authenticateByOu("aaqil", "aaqil", "eSecurity")).thenReturn(true);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/validatebyou")
                .param("username", "aaqil")
                .param("password", "aaqil")
                .param("ou", "eSecurity"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Welcome to the home page!"));
    }

    @Test
    public void testIndex_InvalidCredentials() throws Exception {
        // Mock LDAP authentication to return false
        when(ldapService.authenticateByOu("muthu", "muthu", "MaCaCo")).thenReturn(false);

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/validatebyou")
                .param("username", "muthu")
//          +     .param("password", "muthu")
                .param("ou", "MaCaCo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Invalid!"));
    }
//    @Test
//    public void testLoginvalidate_InvalidCredentials_ReturnsInvalidMessage() throws Exception {
//        // given - mock setup
//        String username = "testuser";
//        String password = "testpassword";
//        
//        
//        when(LdapService.authenticate(username, password)).thenReturn(false);
//
//        // when - performing the requesst
//        mockMvc.perform(get("/loginvalidate")
//                .param("username", username)
//                .param("password", password))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Invalid!"));
//        
//        verify(ldapService);
//		// then - verify method invocation
//        LdapService.authenticate(username, password);
//    }
}


