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

        ZooKeeper zoo = new ZooKeeper("localhost:3000", 3000, null);
        ZookeeperWatcher zookeeperWatcher = new ZookeeperWatcher(zoo, confStorage);

    }
}
