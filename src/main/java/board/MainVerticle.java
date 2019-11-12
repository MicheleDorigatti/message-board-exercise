package board;

import io.netty.util.concurrent.Promise;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {
    HttpServer server;

  @Override
  public void start(Future<Void> startFuture) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    setRoutes(router);
    server = vertx
            .createHttpServer()
            .requestHandler(router)
            .listen(8080, result -> {
              if (result.succeeded()) {
                System.out.println("Board server started");
                startFuture.complete();
              } else {
                startFuture.fail(result.cause());
              }
            });
  }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        server.close();
        stop();
        stopFuture.complete();
    }

  private void setRoutes(Router router) {
      // Get to root
      router.get("/").handler(req -> {
          int client = 0;
          JsonObject json = new JsonObject()
                  .put("client", client)
                  .put("links", new JsonArray()
                          .add(new JsonObject()
                                  .put("href", "/board")
                                  .put("rel", "list")
                                  .put("method", "GET"))
                          .add(new JsonObject()
                                  .put("href", "/board/" + client)
                                  .put("rel", "create")
                                  .put("method", "POST")));
        req.response()
                .putHeader("content-type", "application/json")
                .end(json.encode());
      });

      // A client can view all messages in the service
      router.get("/board").handler(req -> {
          JsonObject json = new JsonObject()
                  .put("messages", new JsonArray()); // TODO: read the messages
          req.response()
                  .putHeader("content-type", "application/json")
                  .end(json.encode());
      });

      // A client can create a message in the service
      router.post("/board/:client").handler(req -> {
          int client = Integer.parseInt(req.request().getParam("client"));
          JsonObject req_json = req.getBodyAsJson();
          System.out.println(req_json.encodePrettily());
          String text = req_json.getString("text");

          req.response()
                  .putHeader("content-type", "application/json")
                  .end(req_json.encode());
      });

      // A client can modify their own messages
      // A client can delete their own messages

  }
}



