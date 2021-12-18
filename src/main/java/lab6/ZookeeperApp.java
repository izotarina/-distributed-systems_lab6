package lab6;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;
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


        }
    }
}
