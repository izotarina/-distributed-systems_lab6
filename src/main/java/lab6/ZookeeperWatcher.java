package lab6;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperWatcher implements Watcher {
    private final ZooKeeper zoo;
    private final ActorRef confStorage;

    public ZookeeperWatcher(ZooKeeper zoo, ActorRef confStorage) {
        this.zoo = zoo;
        this.confStorage = confStorage;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zoo.create("/servers/s",
                    "data".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE ,
                    CreateMode.EPHEMERAL_SEQUENTIAL
            );
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        List<String> servers = new ArrayList<>();
        try {
            servers = zoo.getChildren("/servers", this);

            for (String s: servers) {
                byte[] data = zoo.getData("/servers/" + s, false, null);
                System.out.println("server " + s + " data=" + new String(data));

                servers.add(new String(data));
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        Patterns.ask(confStorage, new ServersList(servers), 3000);
    }
}
