package lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;

public class ZookeeperWatcher implements Watcher {
    private final ZooKeeper zoo;
    private final ActorRef confStorage;

    private final static String SERVERS_PATH = "/servers";
    private final static String  = "/servers";

    public ZookeeperWatcher(ZooKeeper zoo, ActorRef confStorage) throws InterruptedException, KeeperException {
        this.zoo = zoo;
        this.confStorage = confStorage;

        sendServers();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            sendServers();
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void sendServers() throws InterruptedException, KeeperException {
        List<String> serverUrls = new ArrayList<>();
        List<String> servers = zoo.getChildren("/servers", this);

        for (String s: servers) {
            byte[] data = zoo.getData("/servers/" + s, false, null);
            System.out.println("server " + s + " data=" + new String(data));

            serverUrls.add(new String(data));
        }
        confStorage.tell(new ServersList(serverUrls), ActorRef.noSender());
    }
}
