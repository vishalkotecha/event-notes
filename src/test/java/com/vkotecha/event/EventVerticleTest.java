package com.vkotecha.event;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vishal Kotecha
 */
@RunWith(VertxUnitRunner.class)
public class EventVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext context) throws Exception {
    vertx = Vertx.vertx();
    vertx.deployVerticle(EventVerticle.class.getName(), context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) throws Exception {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testApplication(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/",
        response -> {
          response.handler(body -> {
            context.assertTrue(body.toString().contains("Hello"));
            async.complete();
          });
        });
  }
}