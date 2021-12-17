package lab6;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public class ZookeeperApp {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("test");
        ActorRef router = system.actorOf(Props.create(HttpServer.class));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
    }
}
