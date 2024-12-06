package ru.t1.school.open.project.application.environment.postgres;

import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostgresTestContainer {
    @Container
    private final PostgreSQLContainer<?> postgres;

    {
        postgres = new PostgreSQLContainer<>("postgres:15.3")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");

        postgres.start();
    }

    @DynamicPropertySource
    void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    public PostgreSQLContainer<?> getPostgres() {
        return postgres;
    }
}