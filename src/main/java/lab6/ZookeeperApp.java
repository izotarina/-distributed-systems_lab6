package lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

public class ZookeeperApp {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ActorSystem system = ActorSystem.create("test");
        ActorRef confStorage = system.actorOf(Props.create(ConfStorageActor.class));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        ZooKeeper zoo = new ZooKeeper(args[0], 3000, null);
        ZookeeperWatcher zookeeperWatcher = new ZookeeperWatcher(zoo, confStorage);


        ArrayList<CompletionStage<ServerBinding>> bindings = new ArrayList<>();
        for (int i = 1; i < args.length; ++i) {
            HttpServer server = new HttpServer(confStorage, http, zoo, args[i]);

            final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
            final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                    routeFlow,
                    ConnectHttp.toHost("localhost", Integer.parseInt(args[i])),
                    materializer
            );
            bindings.add(binding);
        }

        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();

        for (int i = 0; i < bindings.size(); ++i) {
            bindings.get(i).thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate())
        }
    }
}
