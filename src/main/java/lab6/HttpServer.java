package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

public class HttpServer implements Watcher {
    private final ActorRef confStorageActor;
    private final Http http;
    private final String path;
    private final ZooKeeper zoo;

    public HttpServer(ActorRef confStorageActor, Http http, ZooKeeper zoo, String port) {
        this.confStorageActor = confStorageActor;
        this.http = http;
        this.path = "localhost:" + port;

    }

    private Route createRoute() {
        return route(
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

                                return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                            }
                        })
                    )
                )
            )
        );
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
