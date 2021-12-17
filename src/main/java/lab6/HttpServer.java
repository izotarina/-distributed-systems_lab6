package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;
import static akka.http.javadsl.server.Directives.complete;

public class HttpServer {

    private Route createRoute(ActorRef routerActor) {
        Route router = route(
                path("result", () ->
                        get(() ->
                                parameter("packageId", (packageId) ->
                                {
                                    Future<Object> result = Patterns.ask(routerActor, new GetTestResults(packageId), 5000);
                                    return completeOKWithFuture(result, Jackson.marshaller());
                                }))),
                path("test", () ->
                        route(
                                post(() ->
                                        entity(Jackson.unmarshaller(InputDataTests.class), msg -> {
                                            routerActor.tell(msg, ActorRef.noSender());
                                            return complete("Test started!");
                                        })))));
        return router;
    }
}
