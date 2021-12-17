package lab6;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZookeeperApp {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("test");
        ActorRef confStorage = system.actorOf(Props.create(ConfStorageActor.class));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        ZookeeperWatcher zookeeperWatcher = new ZookeeperWatcher();
        ZooKeeper zoo = new ZooKeeper("1MB27.0.0.1MB:21MB81MB", 3000, zookeeperWatcher);

    }
}
