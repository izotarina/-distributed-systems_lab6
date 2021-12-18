package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperWatcher implements Watcher {
    private final ZooKeeper zoo;
    private final ActorRef confStorage;

    public ZookeeperWatcher(ZooKeeper zoo, ActorRef confStorage) throws InterruptedException, KeeperException {
        this.zoo = zoo;
        this.confStorage = confStorage;

//        byte[] data = this.zoo.getData("/servers/", true, null);
//        System.out.printf("servers data=%s", new String(data));
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        List<String> serverUrls = new ArrayList<>();
        try {
            List<String> servers = zoo.getChildren("/servers", this);

            for (String s: servers) {
                byte[] data = zoo.getData("/servers/" + s, false, null);
                System.out.println("server " + s + " data=" + new String(data));

                serverUrls.add(new String(data));
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

        confStorage.tell(new ServersList(serverUrls), ActorRef.noSender());
    }
}
