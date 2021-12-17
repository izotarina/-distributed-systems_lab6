package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

public class HttpServer {

    private Route createRoute(ActorRef routerActor) {
        Route router = route(
                path("", () ->
                        get(() ->
                                parameter("url", (url) ->
                                        parameter("count", (count) -> {

                                        })
                                )))
        return router;
    }
}
