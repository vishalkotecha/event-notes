package com.vkotecha.event;

import com.vkotecha.event.model.Notes;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vishal Kotecha
 */
public class EventVerticle extends AbstractVerticle {
  private JDBCClient jdbc;

  @Override
  public void start(Promise<Void> promise) throws Exception {
    jdbc = JDBCClient.createShared(vertx, config(), "My-Notes-Collection");
    createSomeData();

    Router router = Router.router(vertx);

    router.route("/")
          .handler(context -> {
            HttpServerResponse response = context.response();
            response
                .putHeader("content-type","text/html")
                .end("<h1>Hello from router " +
                    "Vert.x 3 application</h1>");
          });


    router.route("/api/notes").handler(this::getAll);
    router.route("/api/notes*").handler(BodyHandler.create());
    router.post("/api/notes").handler(this::addOne);
    router.get("/api/whiskies/:id").handler(this::getOne);
    router.put("/api/whiskies/:id").handler(this::updateOne);
    router.delete("/api/notes/:id").handler(this::deleteOne);


    router.route("/assets/*").handler(StaticHandler.create("assets"));

    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(
            // Retrieve the port from the configuration,
            // default to 8080.
            config().getInteger("http.port", 8080),
            result -> {
              if (result.succeeded()) {
                promise.complete();
              } else {
                promise.fail(result.cause());
              }
            }
        );
  }



  private void getAll(RoutingContext routingContext) {
    routingContext.response()
        .putHeader("content-type","application/json; charset=utf-8")
        .end(Json.encodePrettily(notes.values()));
  }

  private void addOne(RoutingContext routingContext) {
    final Notes note = Json.decodeValue(routingContext.getBodyAsString(), Notes.class);
    notes.put(note.getId(),note);
    routingContext
        .response()
        .setStatusCode(201)
        .putHeader("content-type","application/json; charset=utf-8")
        .end(Json.encodePrettily(note));
  }

  private void getOne(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      final Long idAsInteger = Long.valueOf(id);
      Notes note = notes.get(idAsInteger);
      if (note == null) {
        routingContext.response().setStatusCode(404).end();
      } else {
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(note));
      }
    }
  }

  private void updateOne(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    JsonObject json = routingContext.getBodyAsJson();
    if (id == null || json == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      final Long idAsInteger = Long.valueOf(id);
      Notes note = notes.get(idAsInteger);
      if (note == null) {
        routingContext.response().setStatusCode(404).end();
      } else {
        note.setTitle(json.getString("name"));
        note.setDescription(json.getString("origin"));
        routingContext.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(note));
      }
    }
  }

  private void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      Long idAsLong = Long.valueOf(id);
      notes.remove(idAsLong);
    }
    routingContext.response().setStatusCode(204).end();
  }

  // Store our product
  private Map<Long, Notes> notes = new LinkedHashMap<>();
  // Create some product
  private void createSomeData() {
    Notes bowmore = new Notes("Title1","Description1");
    notes.put(bowmore.getId(), bowmore);
    Notes talisker = new Notes("Title2","Description2");
    notes.put(talisker.getId(), talisker);
  }

  private void createSomeData(AsyncResult<SQLConnection> result,
                              Handler<AsyncResult<Void>> next, Future<Void> fut) {
    if (result.failed()) {
      fut.fail(result.cause());
    } else {
      SQLConnection connection = result.result();
      connection.execute(
          "CREATE TABLE IF NOT EXISTS Whisky (id INTEGER IDENTITY, name varchar(100), " +
              "origin varchar(100))",
          ar -> {
            if (ar.failed()) {
              fut.fail(ar.cause());
              connection.close();
              return;
            }
            connection.query("SELECT * FROM Whisky", select -> {
              if (select.failed()) {
                fut.fail(ar.cause());
                connection.close();
                return;
              }
              if (select.result().getNumRows() == 0) {
                insert(
                    new Notes("Bowmore 15 Years Laimrig", "Scotland, Islay"),
                    connection,
                    (v) -> insert(new Notes("Talisker 57° North", "Scotland, Island"),
                        connection,
                        (r) -> {
                          next.handle(Future.<Void>succeededFuture());
                          connection.close();
                        }));
              } else {
                next.handle(Future.<Void>succeededFuture());
                connection.close();
              }
            });
          });
    }
  }

  private void insert(Notes whisky, SQLConnection connection, Handler<AsyncResult<Notes>> next) {
    String sql = "INSERT INTO Notes (title, description) VALUES ?, ?";
    connection.updateWithParams(sql,
        new JsonArray().add(whisky.getTitle()).add(whisky.getDescription()),
        (ar) -> {
          if (ar.failed()) {
            next.handle(Future.failedFuture(ar.cause()));
            return;
          }
          UpdateResult result = ar.result();
          // Build a new whisky instance with the generated id.
          Notes w = new Notes(result.getKeys().getLong(0), whisky.getTitle(), whisky.getDescription());
          next.handle(Future.succeededFuture(w));
        });
  }


}
