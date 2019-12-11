package com.vkotecha.event;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * @author Vishal Kotecha
 */
public class EventVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    vertx
        .createHttpServer()
        .requestHandler(r -> {
          r
              .response()
              .end("<h1> Hello world </h1>");
        })
        .listen(8080, result -> {
          if (result.succeeded()) {
            startFuture.complete();
          }else{
            startFuture.fail(result.cause());
          }
        });
  }
}
