package board;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainVerticle extends AbstractVerticle {
    HttpServer server;
    int client_counter = 0;
    Map<Integer, Integer> message_counters = new HashMap();
    Map<Integer, Map<Integer, String>> messages = new HashMap();

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
          // SocketAddress remote = req.request().remoteAddress();
          // System.out.println("remote " + remote);
          client_counter++;
          messages.put(client_counter, new HashMap<Integer, String>());
          message_counters.put(client_counter, 0);
          JsonObject json = new JsonObject()
                  .put("client", client_counter)
                  .put("links", new JsonArray()
                          .add(new JsonObject()
                                  .put("href", "/board")
                                  .put("rel", "list")
                                  .put("method", "GET"))
                          .add(new JsonObject()
                                  .put("href", "/board/" + client_counter)
                                  .put("rel", "create")
                                  .put("method", "POST")));
        req.response()
                .putHeader("content-type", "application/json")
                .end(json.encode());
      });

      // A client can view all messages in the service
      router.get("/board").handler(req -> {
          JsonObject json = new JsonObject()
                  .put("messages", new JsonArray(getMessagesAsList()));
          req.response()
                  .putHeader("content-type", "application/json")
                  .end(json.encode());
      });

      // A client can create a message in the service
      router.post("/board/:client").handler(req -> {
          int client = Integer.parseInt(req.request().getParam("client"));
          message_counters.put(client, message_counters.get(client) + 1);
          int ID = message_counters.get(client);
          JsonObject req_json = req.getBodyAsJson();
          String text = req_json.getString("text");
          messages.get(client).put(ID, text);

          // response
          JsonObject res_json = req_json.copy();
          res_json
                  .put("ID", ID)
                  .put("links", new JsonArray()
                          .add(new JsonObject()
                                  .put("href", "/board/" + client + "/" + ID)
                                  .put("rel", "self")
                                  .put("method", "GET"))
                          .add(new JsonObject()
                                  .put("href", "/board/" + client + "/" + ID)
                                  .put("rel", "delete")
                                  .put("method", "DELETE"))
                          .add(new JsonObject()
                                  .put("href", "/board/" + client + "/" + ID)
                                  .put("rel", "edit")
                                  .put("method", "PATCH")));

          req.response()
                  .putHeader("content-type", "application/json")
                  .end(res_json.encode());
      });

      // A client can modify their own messages
      router.patch("/board/:client/:ID").handler(req -> {
          int client = Integer.parseInt(req.request().getParam("client"));
          int ID = Integer.parseInt(req.request().getParam("ID"));
          JsonObject req_json = req.getBodyAsJson();
          String text = req_json.getString("text");
          String previous_text = messages.get(client).get(ID);
          messages.get(client).put(ID, text);

          // response - we return the previous text
          JsonObject res_json = req_json.copy();
          res_json
                  .put("ID", ID)
                  .put("text", previous_text)
                  .put("links", new JsonArray()
                          .add(new JsonObject()
                                  .put("href", "/board/" + client + "/" + ID)
                                  .put("rel", "self")
                                  .put("method", "GET"))
                          .add(new JsonObject()
                                  .put("href", "/board/" + client + "/" + ID)
                                  .put("rel", "delete")
                                  .put("method", "DELETE"))
                          .add(new JsonObject()
                                  .put("href", "/board/" + client + "/" + ID)
                                  .put("rel", "edit")
                                  .put("method", "PATCH")));

          req.response()
                  .putHeader("content-type", "application/json")
                  .end(res_json.encode());
      });

      // A client can delete their own messages

  }

  private List<JsonObject> getMessagesAsList() {
      List<JsonObject> result = new LinkedList();
      for (Map.Entry<Integer, Map<Integer, String>> entry: messages.entrySet()) {
          Integer client = entry.getKey();
          Map<Integer, String> id2text = entry.getValue();
          for (Map.Entry<Integer, String> entryMsg: id2text.entrySet()) {
              Integer ID = entryMsg.getKey();
              String text = entryMsg.getValue();
             result.add(new JsonObject()
                     .put("client", client)
                     .put("text", text)
                     .put("ID", ID)
             );
          }
      }
      return result;
  }
}



