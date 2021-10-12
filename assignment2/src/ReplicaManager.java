

import java.lang.reflect.Member;
import java.net.InetAddress;
import spread.*;
public class ReplicaManager implements ReplicaManagerInterface{

    private String daemon_address;
    private int port;
    private String group_name;

    private Client[] clients;

    public ReplicaManager()
    {
        this.daemon_address = "127.0.0.1";
        this.port = 4803;
        this.group_name = "group1";
    }

    public void setUpClients(int number_of_clients)
    {
        clients = new Client[number_of_clients];

        for(int i=0;i<number_of_clients;i++)
        {
            int client_id = i+1;
            clients[i] = new Client(client_id);
            clients[i].connectToSpreadDaemon(daemon_address, port);
            clients[i].joinGroup(group_name);
        }
    }


    public void sendMessage(int clientId, byte[] message)
    {
        clients[clientId-1].sendMessage(message);
    }

    public static void main(String[] args) {
        // write your code here

        ReplicaManager manager = new ReplicaManager();
        int number_of_clients = 5;
        manager.setUpClients(number_of_clients);
        manager.sendMessage(1, "hello".getBytes());
        manager.sendMessage(2, "hey".getBytes());
    }
}