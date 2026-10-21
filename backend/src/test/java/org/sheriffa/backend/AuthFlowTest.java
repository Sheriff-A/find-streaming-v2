package org.sheriffa.backend;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

public class AuthFlowTest extends AbstractIntegrationTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private PostgreSQLContainer postgres;

    @Test
    void registerThenCallMe() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@example.com";

        // Mock Register a User
        MvcResult registered = mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {"email": "%s", "password": "password123"}
                                """.formatted(email)
                        )
        ).andExpect(
                status().isCreated()
        ).andReturn();

        // Get the access token
        String accessToken = JsonPath.read(registered.getResponse().getContentAsString(), "$.accessToken");

        // Mock Call Me
        mockMvc.perform(
                get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + accessToken)
        ).andExpect(
                status().isOk()
        ).andExpect(
                // Expect that the user is the one we registered
                jsonPath("$.email").value(email)
        ).andExpect(
                // Expect that the user has roles
                jsonPath("$.roles").isArray()
        ).andExpect(
                jsonPath("$.roles").isNotEmpty()
        ).andExpect(
                // Expect that the user has the default role
                jsonPath("$.roles[0]").value("ROLE_USER")
        );
    }
}
