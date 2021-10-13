import spread.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

public class AccountReplica {
    /*
    Flow:
    1) AccountReplica replica = new AccountReplica();
    2) replica.parseCommandLineArguments(args);
    3) replica.setUpSpreadConstructs();
     */

    private static int numberOfReplicas;
    private static String replicaId = "replica1";
    private static int port = 0;
    private static String serverAddress;
    private static String accountName;
    private static String fileName;

    private static SpreadConnection connection;
    private static SpreadGroup group;

    public static void setUpSpreadConstructs() throws SpreadException, UnknownHostException {
        connection = new SpreadConnection();
        connection.add(new Listener());
        connection.connect(InetAddress.getByName(serverAddress), port, replicaId, false, true);

        group = new SpreadGroup();
        group.join(connection, accountName);
    }

    public static void parseCommandLineArguments(String[] args) {
        serverAddress = args[0];
        accountName = args[1];
        numberOfReplicas = Integer.parseInt(args[2]);

        if (args.length==4) {
            fileName = args[3];
        }
//        System.out.println(accountName);
//        System.out.println(numberOfReplicas);
//        System.out.println(serverAddress);
//        System.out.println(fileName);
    }


    public static void sendCommand(String command) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.addGroup(accountName);
        message.setReliable();
        message.setObject(command);
        connection.multicast(message);
        System.out.println("command : "+ command+" multicasted by : "+replicaId);
    }

    //args:
    // <server address> <account name> <number of replicas> <file name>
    // server address - String eg:- 127.0.0.1
    // account name - String eg:- testaccount
    // number of replicas - int eg:- 3
    // filename - String eg:- testfile

    public static void main(String[] args) {

        Random random = new Random();
        replicaId = Integer.toString(random.nextInt(50));
        System.out.println("replica id : "+replicaId);
        parseCommandLineArguments(args);

        try {
            setUpSpreadConstructs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fileName != null ){
            //parse file
        }

        String command = "";
        while (!command.equals("exit")){
            //keep it running
            Scanner input = new Scanner(System.in);
            command = input.nextLine();

            try {
                sendCommand(command);
            } catch (SpreadException e) {
                e.printStackTrace();
            }
        }




    }

}
