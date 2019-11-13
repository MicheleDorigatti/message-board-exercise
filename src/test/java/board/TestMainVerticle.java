package board;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

    @Test
    @DisplayName("Create a message and list all messages")
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    void test(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);

        // Contact server so a client ID is created
        client.get(8080, "::1", "/")
                .putHeader("Accept", "application/json")
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 2);
                }));

        // Create a message
        JsonObject req_json = new JsonObject()
                .put("client", 1)
                .put("text", "A test message");
        client.post(8080, "::1", "/board/1")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 4);
                }));

        // Try to create a message for another user
        req_json = new JsonObject()
                .put("client", 1)
                .put("text", "A test message");
        client.post(8080, "::1", "/board/2")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    assertEquals(401, response.result().statusCode());
                }));

        showAllMessages(testContext, client);

        // Edit a message
        req_json = new JsonObject()
                .put("client", 1)
                .put("text", "An edited test message");
        client.patch(8080, "::1", "/board/1/1")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 4);
                }));

        // Edit a non existing message
        req_json = new JsonObject()
                .put("client", 1)
                .put("text", "An edited test message");
        client.patch(8080, "::1", "/board/1/100")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    assertEquals(404, response.result().statusCode());
                }));

        showAllMessages(testContext, client);

        // Delete a message
        req_json = new JsonObject()
                .put("client", 1);
        client.delete(8080, "::1", "/board/1/1")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 3);
                }));

        showAllMessages(testContext, client);

        // Try to delete same message again
        req_json = new JsonObject()
                .put("client", 1);
        client.delete(8080, "::1", "/board/1/1")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    assertEquals(404, response.result().statusCode());
                    testContext.completeNow();
                }));
    }

    private void showAllMessages(VertxTestContext testContext, WebClient client) {
        // Show all messages
        client.get(8080, "::1", "/board")
                .putHeader("Accept", "application/json")
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 1);
                }));
    }
}

