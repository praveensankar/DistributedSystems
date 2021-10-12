import spread.BasicMessageListener;
import spread.SpreadConnection;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.net.InetAddress;

public class Client implements BasicMessageListener, ClientInterface {

    private int id=-1;
    private SpreadConnection connection;
    private SpreadGroup group;
    private SpreadMessage message;

    public Client(int id)
    {
        this.id = id;
        this.connection = new SpreadConnection();
        this.connection.add(this);
        group = new SpreadGroup();
        message = new SpreadMessage();
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public void connectToSpreadDaemon(String dameon_address, int port)
    {
        try{
            connection.connect(InetAddress.getByName(dameon_address), port, "replica"+String.valueOf(this.id), false, true);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void disconnectFromSpreadDaemon()
    {
        try{
            connection.disconnect();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void joinGroup(String group_name)
    {
        try{
            group.join(this.connection, group_name);
            this.configureMessage(group_name);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void configureMessage(String group_name)
    {
        try{
        message.addGroup(group_name);
        message.setReliable();

    }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void sendMessage(byte[] data)
    {
        try{
            message.setData(data);
            connection.multicast(message);
        System.out.println("message : "+ new String(data)+" multicasted by : "+this.id);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void messageReceived(SpreadMessage message) {
        if(message.isRegular())
        {
        byte data[] = message.getData();
        System.out.println("id : "+this.id+"\t message :  " + new String(data)+"\t sent by : "+message.getSender());
        }
    }

    public static void main(String[] args)
    {

        String daemon_address = "127.0.0.1";
        int port = 4814;
        String group_name = "group1";
        int client_id = 1;
        byte[] message = "helo".getBytes();
        Client client1 = new Client(client_id);
        client1.connectToSpreadDaemon(daemon_address, port);
        client1.joinGroup(group_name);
        client1.sendMessage(message);
        client1.disconnectFromSpreadDaemon();

    }


}
