package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import scala.concurrent.Future;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

public class HttpServer {
    private final ActorRef confStorageActor;
    private final Http http;

    public HttpServer(ActorRef confStorageActor, Http http) {
        this.confStorageActor = confStorageActor;
        this.http = http;
    }

    private Route createRoute(ActorRef routerActor) {
        Route router = route(
            path("", () ->
                get(() ->
                    parameter("url", (url) ->
                        parameter("count", (count) -> {
                            {
                                if (Integer.parseInt(count) != 0) {
                                    return completeWithFuture(Patterns.ask(confStorageActor, new GetRandomServer(), Duration.ofMillis(5000))
                                        .thenCompose(response -> http.singleRequest(HttpRequest.create(String.format("http://%s/?url=http://%s&count=%d", response, url, Integer.parseInt(count)))))
                                    );
                                }

                                return completeWithFuture(http.singleRequest(url));
                            }
                        })
                    )))
    return router;
    }
}
