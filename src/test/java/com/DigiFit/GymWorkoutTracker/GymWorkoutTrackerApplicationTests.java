package com.DigiFit.GymWorkoutTracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import com.DigiFit.GymWorkoutTracker.service.AuthService;
import java.util.UUID;
import java.util.Collections;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "JWT_SECRET=test-secret-key-for-testing-purposes-only-min-256-bits",
        "supabase.jwt.secret=test-secret-key-for-testing-purposes-only-min-256-bits",
        "SUPABASE_URL=http://localhost:54321",
        "supabase.key=test-key",
        "DATABASE_URL=jdbc:h2:mem:testdb",
        "DATABASE_USERNAME=sa",
        "DATABASE_PASSWORD=",
        "SERVICE_ROLES=dummy-service-role-key"
})
class GymWorkoutTrackerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // Mock the Supabase client
    //@MockBean
    //private SupabaseClient supabaseClient;

    @Test
    void contextLoads() {
    }

    @Test
    void testSignUpEndpoint() throws Exception {
        // This is a sample JSON body for a registration request
        String userJson = "{\"username\":\"testuser\", \"password\":\"testpass\", \"email\":\"test@example.com\"}";

        // Create a fake response that mock AuthService will return
        Map<String, Object> fakeUser = Map.of("username", "testuser", "email", "test@example.com");
        Map<String, Object> fakeServiceResponse = Map.of("user", fakeUser);

        when(authService.signUp(any(String.class), any(String.class), any(String.class)))
                .thenReturn(fakeServiceResponse);

        mockMvc.perform(
                // perform a post request
                post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                )
                // Check the results
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("testuser")); // Check that the response JSON has the right username
    }

    @Test
    void testLoginEndpoint() throws Exception {
        // 1. Define the JSON body for the login request
        String loginJson = "{\"email\":\"test@example.com\", \"password\":\"testpass\"}";

        // 2. Define the fake response from the authService
        Map<String, Object> fakeUser = Map.of("username", "testuser", "email", "test@example.com");
        Map<String, Object> fakeLoginResponse = Map.of(
                "access_token", "fake-jwt-access-token-12345",
                "user", fakeUser
        );

        // 3. Set up the mock
        when(authService.login(any(String.class), any(String.class)))
                .thenReturn(fakeLoginResponse);

        // 4. Perform the request and check the results
        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("fake-jwt-access-token-12345"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void testLoginEndpoint_WithBadCredentials_ShouldFail() throws Exception {
        // 1. Define the JSON body for the login request
        String loginJson = "{\"email\":\"test@example.com\", \"password\":\"wrongpass\"}";

        // 2. Set up the mock to throw an exception
        // We'll simulate the service throwing an "Unauthorized" exception
        when(authService.login(any(String.class), any(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Bad credentials"));

        // 3. Perform the request and check the results
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                // We expect the server to return a 401 Unauthorized status
                .andExpect(status().isUnauthorized())
                // Optionally, check the error message in the response body
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    // TEST FOR A SECURED ENDPOINT (SUCCESS)
    @Test
    void testVerifyEndpoint_WithMockUser_ShouldSucceed() throws Exception {
        // 1. Create a fake UUID for our mock user
        UUID testUserId = UUID.randomUUID();

        // 2. Create a mock Authentication object that matches what your
        // JwtAuthFilter would create. The "principal" is the user's ID.
        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                testUserId, // This is the 'principal'
                null,
                Collections.emptyList()
        );

        // 3. Perform the request and use .with(authentication(...))
        // to inject the fake user into the Security Context.
        mockMvc.perform(
                        get("/api/auth/verify") // This is a GET request
                                .with(authentication(mockAuthentication))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.userId").value(testUserId.toString()));
    }

    // TEST THAT THE ENDPOINT IS SECURE (FAILURE)
    @Test
    void testVerifyEndpoint_NoUser_ShouldFailWith403() throws Exception {

        mockMvc.perform(get("/api/auth/verify"))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden
    }
}