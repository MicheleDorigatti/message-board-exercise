package board;

import io.vertx.core.Vertx;
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
    private MainVerticle verticle;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
      verticle = new MainVerticle();
    vertx.deployVerticle(verticle, testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  @DisplayName("")
  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  void test(Vertx vertx, VertxTestContext testContext) {
  }

}
