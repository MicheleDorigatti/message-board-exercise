package board;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
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
    void createAndShow(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);

        // Contact server so a client ID is created
        client.get(8080, "::1", "/")
                .putHeader("Accept", "application/json")
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 2);
                    System.out.println("body\n" + response.result().bodyAsJsonObject().encodePrettily());
                }));

        // Create a message
        JsonObject req_json = new JsonObject()
                .put("client", "1")
                .put("text", "A test message");
        client.post(8080, "::1", "/board/1")
                .putHeader("Accept", "application/json")
                .sendJsonObject(req_json, response -> testContext.verify(() -> {
                    System.out.println("body\n" + response.result().bodyAsJsonObject().encodePrettily());
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 4);
                }));

        // Show all messages
        client.get(8080, "::1", "/board")
                .putHeader("Accept", "application/json")
                .send(response -> testContext.verify(() -> {
                    assertEquals(200, response.result().statusCode());
                    assertEquals(response.result().bodyAsJsonObject().size(), 1);
                    System.out.println("body list\n" + response.result().bodyAsJsonObject().encodePrettily());
                    testContext.completeNow();
                }));
    }
}

