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
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

public class ZookeeperApp {
    private final static String ACTOR_SYSTEM = "test";
    private final static int TIMEOUT = 5000;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        ActorSystem system = ActorSystem.create(ACTOR_SYSTEM);
        ActorRef confStorage = system.actorOf(Props.create(ConfStorageActor.class));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        ZooKeeper zoo = null;

        try {
            zoo = new ZooKeeper(args[0], TIMEOUT, null);
            new ZookeeperWatcher(zoo, confStorage);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

        ArrayList<CompletionStage<ServerBinding>> bindings = new ArrayList<>();
        StringBuilder info = new StringBuilder();
        for (int i = 1; i < args.length; ++i) {
            try {
                String port = args[i];
                HttpServer server = new HttpServer(confStorage, http, zoo, port);

                final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
                final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                        routeFlow,
                        ConnectHttp.toHost("localhost", Integer.parseInt(port)),
                        materializer
                );
                bindings.add(binding);
                info.append("http://localhost:").append(port).append("/\n");
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(info + "Press RETURN to stop...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        for (CompletionStage<ServerBinding> binding : bindings) {
            binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
        }
    }
}
