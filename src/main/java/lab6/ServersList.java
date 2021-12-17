package lab6;

import java.util.ArrayList;

public class ServersList {
    private final ArrayList<String> servers;

    public ServersList(ArrayList<String> servers) {
        this.servers = servers;
    }

    public ArrayList<String> getServers() {
        return servers;
    }
}
