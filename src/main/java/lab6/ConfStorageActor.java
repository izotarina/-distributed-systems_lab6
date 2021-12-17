package lab6;

import akka.actor.AbstractActor;
import akka.japi.Pair;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.Random;

public class ConfStorageActor extends AbstractActor {
    private ArrayList<String> servers = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(ServersList.class, m -> {
                servers = m.getServers();
            })
            .match(GetRandomServer.class, req -> {
                    sender().tell(random.nextInt(servers.size()), self());
                }
            ).build();
    }
}
