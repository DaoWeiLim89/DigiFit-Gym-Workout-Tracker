package com.DigiFit.GymWorkoutTracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "JWT_SECRET=test-secret-key-for-testing-purposes-only-min-256-bits",
        "supabase.jwt.secret=test-secret-key-for-testing-purposes-only-min-256-bits",
        "supabase.url=http://localhost:54321",
        "supabase.key=test-key"
})
class GymWorkoutTrackerApplicationTests {

    // Mock the Supabase client if you have one
    // Uncomment if you have a SupabaseClient bean
    // @MockBean
    // private SupabaseClient supabaseClient;

    @Test
    void contextLoads() {
    }

}