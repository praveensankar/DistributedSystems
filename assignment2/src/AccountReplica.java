import spread.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

public class AccountReplica implements AdvancedMessageListener {
    /*
    Flow:
    1) AccountReplica replica = new AccountReplica();
    2) replica.parseCommandLineArguments(args);
    3) replica.setUpSpreadConstructs();
     */

    private int numberOfReplicas;
    private String replicaId = "replica1";
    private int port = 0;
    private String serverAddress;
    private String accountName;
    private String fileName;

    private SpreadConnection connection;
    private SpreadGroup group;

    public AccountReplica(String replicaId) {
        this.replicaId = replicaId;
    }
    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        try {
            System.out.println("test message received");
            System.out.println(spreadMessage.getObject().toString());
        } catch (SpreadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        System.out.println(spreadMessage.getMembershipInfo().getGroup());
    }

    public void setUpSpreadConstructs() throws SpreadException, UnknownHostException {
        connection = new SpreadConnection();
        connection.add(this);
        connection.connect(InetAddress.getByName(serverAddress), port, replicaId, false, true);

        group = new SpreadGroup();
        group.join(connection, accountName);
    }

    public void parseCommandLineArguments(String[] args) {
        serverAddress = args[0];
        accountName = args[1];
        numberOfReplicas = Integer.parseInt(args[2]);

        if (args.length==4) {
            fileName = args[3];
        }
        System.out.println(accountName);
//        System.out.println(numberOfReplicas);
//        System.out.println(serverAddress);
//        System.out.println(fileName);
    }


    public void sendCommand(String command) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.addGroup(accountName);
        message.setReliable();
        message.setObject(command);
        connection.multicast(message);
        System.out.println("command : "+ command+" multicasted by : "+replicaId);
    }


    public static void main(String[] args) {

        Random random = new Random();
        int replicaId1 = random.nextInt();
        int replicaId2 = random.nextInt();
        AccountReplica replica1 = new AccountReplica(Integer.toString(replicaId1));
        AccountReplica replica2 = new AccountReplica(Integer.toString(replicaId2));
        replica1.parseCommandLineArguments(args);
        replica2.parseCommandLineArguments(args);
        try {
            replica1.setUpSpreadConstructs();
            replica2.setUpSpreadConstructs();
            replica1.sendCommand("test command");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scanner input = new Scanner(System.in);
        input.next();


    }

}
