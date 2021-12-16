package lab6;

import akka.actor.AbstractActor;
import akka.japi.Pair;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class ConfStorageActor extends AbstractActor {
    private final Map<Pair<String, Integer>, StoreTestResult> store = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(StoreTestResult.class, m -> {
                    Pair<String, Integer> key = new Pair<>(m.getUrl(), m.getRequestCount());
                    store.put(key, m);
                })
                .match(GetTestResult.class, req -> {
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
