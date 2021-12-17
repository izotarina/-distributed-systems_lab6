package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

public class HttpServer {
    private final ActorRef confStorageActor;

    public HttpServer(ActorRef confStorageActor) {
        this.confStorageActor = confStorageActor;
    }

    private Route createRoute(ActorRef routerActor) {
        Route router = route(
            path("", () ->
                get(() ->
                    parameter("url", (url) ->
                        parameter("count", (count) -> {
                            {
                                if (count != 0) {
                                    return completeWithFuture(Patterns.ask())
                                }
                                Future<Object> result = Patterns.ask(routerActor, new GetTestResults(packageId), 5000);
                                return completeOKWithFuture(result, Jackson.marshaller());
                            }
                        })
                    )))
    return router;
    }
}
