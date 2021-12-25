package lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.*;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

public class HttpServer implements Watcher {
    private final static String HOST = "localhost:";
    private final static String URL_PARAMETER = "url";
    private final static String COUNT_PARAMETER = "count";
    private final static String SEGMENT_PATH = "";
    private final static String REQUEST_FORMAT = "http://%s/?url=%s&count=%d";
    private final static Duration TIMEOUT = Duration.ofMillis(5000);
    private final static String INITIAL_ZOOKEEPER_PATH = "/servers/";
    private final static String ZERO_COUNT = "0";

    private final ActorRef confStorageActor;
    private final Http http;
    private final String path;
    private final ZooKeeper zoo;

    public HttpServer(ActorRef confStorageActor, Http http, ZooKeeper zoo, String port) throws InterruptedException, KeeperException {
        this.confStorageActor = confStorageActor;
        this.http = http;
        this.path = HOST + port;
        this.zoo = zoo;
        zoo.create(INITIAL_ZOOKEEPER_PATH + path,
                path.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE ,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    public Route createRoute() {
        return route(
            path(SEGMENT_PATH, () ->
                route(get(() ->
                    parameter(URL_PARAMETER, (url) ->
                        parameter(COUNT_PARAMETER, (count) -> {
                            {
                                System.out.println("Count = " + count + " on " + path);
                                if (!count.equals(ZERO_COUNT)) {
                                    return completeWithFuture(Patterns.ask(confStorageActor, new GetRandomServer(), TIMEOUT)
                                        .thenCompose(response -> {
                                            String format = String.format(REQUEST_FORMAT, response, url, Integer.parseInt(count) - 1);
                                            return http.singleRequest(HttpRequest.create(format));
                                        })
                                    );
                                }

                                return completeWithFuture(http.singleRequest(HttpRequest.create(url)));
                            }
                        })
                    )
                ))
            )
        );
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zoo.getData(path, this, null);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
