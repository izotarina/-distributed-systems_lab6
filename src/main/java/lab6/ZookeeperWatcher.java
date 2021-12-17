package lab6;

import org.apache.zookeeper.*;

import java.util.List;

public class ZookeeperWatcher implements Watcher {
    private final static ZooKeeper zoo;

    public ZookeeperWatcher(ZooKeeper zoo) {
        this.zoo = zoo;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        zoo.create("/servers/s",
                "data".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE ,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
        List<String> servers = zoo.getChildren("/servers", this);
        for (String s: servers) {
            byte[] data = zoo.getData("/servers/" + s, false, null);
            System.out.println("server " + s + " data=" + new String(data));
        }
    }
}
