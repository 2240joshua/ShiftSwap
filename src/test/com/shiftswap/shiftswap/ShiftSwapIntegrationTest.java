package com.shiftswap.shiftswap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShiftSwapIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("shiftswap_test")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);

    // make schema auto-create in test
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }

  @LocalServerPort
  int port;

  private final HttpClient client = HttpClient.newHttpClient();

  @Test
  void endToEnd_swapRequest_accept_approve_reassignsShift() throws Exception {
    // 1) create users
    long aliceId = createUser("Alice", "alice@test.com", "EMPLOYEE");
    long bobId   = createUser("Bob",   "bob@test.com",   "EMPLOYEE");

    // 2) create shift + assign to Alice
    long shiftId = createShift("2026-02-01", "10:00", "14:00", "CASHIER");
    assignShift(shiftId, aliceId);

    // 3) create swap request (Alice -> Bob for that shift)
    long swapId = createSwapRequest(shiftId, aliceId, bobId);

    // 4) Bob accepts
    post("/shiftSwap/" + swapId + "/accept?userId=" + bobId, "");

    // 5) Manager approves
    post("/shiftSwap/" + swapId + "/approve", "");

    // 6) verify the shift is now assigned to Bob (GET shift)
    String shiftJson = get("/shifts/" + shiftId);
    assertTrue(shiftJson.contains("\"id\":" + bobId) || shiftJson.contains("\"assignedUser\":{\"id\":" + bobId),
        "Expected shift to be reassigned to Bob. Got: " + shiftJson);
  }

  // -------- helper methods --------

  private long createUser(String name, String email, String role) throws Exception {
    String body = """
      {"name":"%s","email":"%s","role":"%s"}
      """.formatted(name, email, role);

    String json = post("/users", body);
    return extractId(json);
  }

  private long createShift(String date, String startTime, String endTime, String role) throws Exception {
    String body = """
      {"date":"%s","startTime":"%s","endTime":"%s","role":"%s"}
      """.formatted(date, startTime, endTime, role);

    String json = post("/shifts", body);
    return extractId(json);
  }

  private void assignShift(long shiftId, long userId) throws Exception {
    post("/shifts/" + shiftId + "/assign/" + userId, "");
  }

  private long createSwapRequest(long shiftId, long fromUserId, long toUserId) throws Exception {
    // matches your ShiftSwapController: POST /shiftSwap body expects shiftId, fromUserId, toUserId
    String body = """
      {"shiftId":%d,"fromUserId":%d,"toUserId":%d}
      """.formatted(shiftId, fromUserId, toUserId);

    String json = post("/shiftSwap", body);
    return extractId(json);
  }

  private String baseUrl() {
    return "http://localhost:" + port;
  }

  private String post(String path, String body) throws Exception {
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl() + path))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
    assertTrue(resp.statusCode() >= 200 && resp.statusCode() < 300,
        "POST " + path + " failed: " + resp.statusCode() + " body=" + resp.body());
    return resp.body();
  }

  private String get(String path) throws Exception {
    HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl() + path))
        .GET()
        .build();

    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
    assertTrue(resp.statusCode() >= 200 && resp.statusCode() < 300,
        "GET " + path + " failed: " + resp.statusCode() + " body=" + resp.body());
    return resp.body();
  }

  // super simple id extraction: looks for `"id":123`
  private long extractId(String json) {
    int i = json.indexOf("\"id\":");
    if (i == -1) throw new IllegalArgumentException("No id in response: " + json);
    int start = i + 5;
    int end = start;
    while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
    return Long.parseLong(json.substring(start, end));
  }
}
