import spread.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AccountReplica {
    /*
    Flow:
    1) AccountReplica replica = new AccountReplica();
    2) replica.parseCommandLineArguments(args);
    3) replica.setUpSpreadConstructs();
     */

    // ---------------------------------------------------
    // spread related variables
    // --------------------------------------------------
    private static int numberOfReplicas;
    private static String replicaId = "replica1";
    private static int port = 0;
    private static String serverAddress;
    private static String accountName;
    private static String fileName;

    private static SpreadConnection connection;
    private static SpreadGroup group;


    //---------------------------------------------------
    // bank account state replicated machine related variables
    //----------------------------------------------------
    private static double balance = 0.0;
    private static int orderCounter = 0;
    private static int outstandingCounter = 0;
    private static ArrayList<Transaction> executedList=new ArrayList<Transaction>();
    private  static ArrayList<Transaction> outstandingCollection=new ArrayList<Transaction>();


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


    public static void multicastTransaction(Transaction transaction) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.addGroup(accountName);
        message.setReliable();
        message.setObject(transaction);
        connection.multicast(message);
        System.out.println("transaction : "+ transaction.toString()+" multicasted by : "+replicaId);
    }

    public static void parseCommand(String cmd)  {

        if (cmd.equals("getQuickBalance")){
            getQuickBalance();
        }
        else if(cmd.equals("getSyncedBalance")){
            // Todo: Naive implementation : execute the transactions from the outstanding collection
            getSyncedBalance();
        }
        else if(cmd.equals("getHistory")){
            getHistory();
        }
        else if(cmd.equals("cleanHistory")){

        }
        else if(cmd.equals("memberInfo")){

        }
        else if(cmd.equals("exit")){

        }
        else if(cmd.startsWith("deposit")){
            double amount = Double.parseDouble(cmd.split(" ")[1]);
            // Todo: add transaction object in the oustanding collection and multicast it
            addTransactionToOutstandingCollection(cmd);
            // deposit(amount);
        }
        else if(cmd.startsWith("addInterest")){

        }
        else if(cmd.startsWith("checkTxStatus")){

        }
        else if(cmd.startsWith("sleep")){

        }

    }

    public static void addTransactionToOutstandingCollection(String cmd){
        String uniqueId = replicaId + outstandingCounter;
        Transaction transaction = new Transaction(cmd, uniqueId);
        outstandingCollection.add(transaction);
     //   System.out.println(transaction.toString()+" is added to the outstanding collection");
        outstandingCounter += 1;
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

        // sending outstanding collections to other members in the group
        Runnable sendOutstandingCollection = new Runnable() {
            public void run() {
                // Todo: send the outstanding collection
                while(!outstandingCollection.isEmpty()) {
                    Transaction transaction = outstandingCollection.remove(0);
                        try {
                            multicastTransaction(transaction);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                }

            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(sendOutstandingCollection, 0, 10, TimeUnit.SECONDS);

        if (fileName != null ){
            //parse file
        }

        String command = "";
        while (true){
            //keep it running
            Scanner input = new Scanner(System.in);
            command = input.nextLine();
            if(command.equals("exit")) {
                break;
            }
            try {
                parseCommand(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




    }

    public static void getQuickBalance(){
        System.out.println("balance : "+balance);
    }

    public static void getSyncedBalance(){
        // Todo: do the sync part
        System.out.println("synced balance : "+ balance);
    }

    public static void deposit(double amount){
    balance = balance + amount;
    }
    public static void addInterest(double percent){
        balance  = balance + (balance*percent);
    }

    public static void getHistory(){
        // print the execute list
        System.out.println("executed list : ");
        for(Transaction transaction : executedList) {
            System.out.println(transaction.toString());
        }
        // print the outstanding collection
        System.out.println("outstanding collection : ");
        for(Transaction transaction : outstandingCollection)
        {
            System.out.println(transaction.toString());
        }
    }

    public void checkTxStatus(String uniqueId){

    }
    public void cleanHistory(){

    }
    public String memberInfo(){
        return null;
    }
    public void sleep(int duration){

    }
    public void exit(){

    }

}
