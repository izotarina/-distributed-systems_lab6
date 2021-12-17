package lab6;

import java.util.List;

public class ServersList {
    private final List<String> servers;

    public ServersList(List<String> servers) {
        this.servers = servers;
    }

    public List<String> getServers() {
        return servers;
    }
}
