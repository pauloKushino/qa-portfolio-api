package com.paulo.qa.portfolio.contract;

import com.paulo.qa.portfolio.QaPortfolioApiApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = QaPortfolioApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProjectContractTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("qa_portfolio")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    void shouldCreateProject() {
        String requestBody = """
            {
                "name": "Contract Test Project",
                "description": "Created via contract test",
                "repositoryUrl": "https://github.com/test",
                "demoUrl": "https://demo.com",
                "techStack": "Java, Spring Boot"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Contract Test Project"))
                .body("techStack", equalTo("Java, Spring Boot"));
    }

    @Test
    void shouldGetAllProjects() {
        given()
                .when()
                .get("/projects")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    void shouldGetProjectById() {
        // First create a project
        String createBody = """
            {
                "name": "Get By ID Project",
                "techStack": "Java"
            }
            """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Then get it by ID
        given()
                .when()
                .get("/projects/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Get By ID Project"));
    }

    @Test
    void shouldUpdateProject() {
        String createBody = """
            {
                "name": "To Update",
                "techStack": "Old"
            }
            """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateBody = """
            {
                "name": "Updated Project",
                "techStack": "New Stack"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/projects/{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Project"))
                .body("techStack", equalTo("New Stack"));
    }

    @Test
    void shouldDeleteProject() {
        String createBody = """
            {
                "name": "To Delete",
                "techStack": "Java"
            }
            """;

        Long id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/projects/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/projects/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn404ForNonExistentProject() {
        given()
                .when()
                .get("/projects/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldValidateRequiredFields() {
        String invalidBody = """
            {
                "description": "Missing name"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidBody)
                .when()
                .post("/projects")
                .then()
                .statusCode(400)
                .body("errors.name", notNullValue());
    }
}