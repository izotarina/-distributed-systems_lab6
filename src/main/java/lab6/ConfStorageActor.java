package lab6;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfStorageActor extends AbstractActor {
    private List<String> servers = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(ServersList.class, m -> {
                servers = m.getServers();
            })
            .match(GetRandomServer.class, req -> {
                System.out.println(servers);
                    sender().tell(servers.get(random.nextInt(servers.size())), self());
                }
            ).build();
    }
}
