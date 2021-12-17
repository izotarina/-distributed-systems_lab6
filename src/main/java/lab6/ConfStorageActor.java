package lab6;

import akka.actor.AbstractActor;
import akka.japi.Pair;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;

public class ConfStorageActor extends AbstractActor {
    private ArrayList<String> servers = new ArrayList<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(ServersList.class, m -> {
                    servers = m.getServers();
                })
                .match(GetRandomServer.class, req -> {
                            Pair<String, Integer> key = new Pair<>(req.getUrl(), req.getRequestCount());
                            StoreTestResult result = null;

                            if (store.containsKey(key)) {
                                result = store.get(key);
                            }
                            sender().tell(result, self());
                        }
                ).build();
    }
}
