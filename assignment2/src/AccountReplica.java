import spread.*;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static volatile int orderCounter = 0;
    private static volatile  int outstandingCounter = 0;
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
        //message.setReliable();
        message.setFifo();
        message.setObject(transaction);
       // System.out.println("1 transaction : "+ transaction.toString()+" before multicasted by : "+replicaId);
        connection.multicast(message);
       // System.out.println("2 transaction : "+ transaction.toString()+" after multicasted by : "+replicaId);

    }

    public static void parseFileArguments(String fileName) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File("input/"+fileName));
        while (scanner.hasNextLine()){
            //Read and execute command
            String command = scanner.nextLine();
            parseCommand(command);
          //  System.out.println("command: " + command);
        }
        scanner.close();
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
            cleanHistory();
        }
        else if(cmd.equals("memberInfo")){

        }
        else if(cmd.equals("exit")){

        }
        else if(cmd.startsWith("deposit") || cmd.startsWith("addInterest")){
            //double amount = Double.parseDouble(cmd.split(" ")[1]);
            addTransactionToOutstandingCollection(cmd);
            // deposit(amount);
        }
        else if(cmd.startsWith("checkTxStatus")){
            String uniqueId = cmd.split(" ")[1];
            checkTxStatus(uniqueId);
        }
        else if(cmd.startsWith("sleep")){

        }

    }

    public static void addTransactionToOutstandingCollection(String cmd){
        String uniqueId = replicaId + outstandingCounter;
        Transaction transaction = new Transaction(cmd, uniqueId);
        synchronized(outstandingCollection) {
            outstandingCollection.add(transaction);
        }

        System.out.println(transaction.toString()+" is added to the outstanding collection");
        outstandingCounter += 1;
    }


    //args:
    // <server address> <account name> <number of replicas> <file name>
    // server address - String eg:- 127.0.0.1
    // account name - String eg:- testaccount
    // number of replicas - int eg:- 3
    // filename - String eg:- testfile


    public static void main(String[] args) throws FileNotFoundException {

        Random random = new Random();
        replicaId = Integer.toString(random.nextInt(50));
        System.out.println("replica id : "+replicaId);
        parseCommandLineArguments(args);

        if (fileName != null ){
            //parse file
            replicaId = "primary";
        }

        try {
            setUpSpreadConstructs();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // sending outstanding collections to other members in the group
        Runnable sendOutstandingCollection = new Runnable() {
            public void run() {
                // Todo: send the outstanding collection

                    for (Transaction transaction: outstandingCollection) {

                        try {
                            multicastTransaction(transaction);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(sendOutstandingCollection, 0, 1, TimeUnit.SECONDS);

        if (fileName != null ){
            //parse file
            parseFileArguments(fileName);

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
        System.out.println("quick balance : "+balance);
    }

    public static void getSyncedBalance() {
        // Todo: do the sync part
        // naive implementation: we are checking whether the outstanding collection is empty or not and running it in a
        // infinite loop. once the outstanding collection is empty then we will print the balance

        // corner case: after get synced balance is called, it blocks the current execution. so new deposits or
        // add interest commands won't be added to the outstanding collection till the get synced balance is finished
        System.out.println("synced balance is called");

        do{
            if(outstandingCounter == orderCounter){
                break;
            }
        }while(true);

        // System.out.println("outstanding counter : " + outstandingCounter + "\t order counter : " + orderCounter);
        if (orderCounter == outstandingCounter) {
            System.out.println("synced balance : " + balance);
        }
    }

    public static void deposit(double amount){
    balance = balance + amount;
    }
    public static void addInterest(double percent){
        balance  = balance + ((balance*percent)/100);
    }

    public static void getHistory(){
        // print the execute list
        System.out.println("-------------start history----------------");
        System.out.println("executed list : ");
        int counter = orderCounter - executedList.size();
        for(Transaction transaction : executedList) {
            System.out.println(counter + " : "+transaction.toString());
            counter++;
        }
        // print the outstanding collection
        System.out.println("outstanding collection : ");
        for(Transaction transaction : outstandingCollection)
        {
            System.out.println(transaction.toString());
        }
        System.out.println("-------------end history----------------");
    }

    public static void checkTxStatus(String uniqueId){
        synchronized (outstandingCollection){
            for(Transaction transaction: outstandingCollection)
            {
                if(transaction.getUnique_id().equals(uniqueId))
                {
                    System.out.println("Transaction is in outstandinCollection: " + uniqueId);
                    return;
                }
            }
        }
        synchronized (executedList){
            for(Transaction transaction: executedList)
            {
                if(transaction.getUnique_id().equals(uniqueId))
                {
                    System.out.println("Transaction is in executedList: " + uniqueId);
                    return;
                }
            }
        }
        System.out.println("UniqueId isn't found: " + uniqueId);
    }
    public static void cleanHistory(){
        synchronized (executedList){
            executedList = new ArrayList<>();
            System.out.println("executedList size: " + executedList.size());
        }

    }
    public static void memberInfo(){
    }
    public static void sleep(int duration){

    }
    public static void exit(){

    }



    public static Transaction removeTransactionFromOutstandingCollection()
    {
        synchronized (outstandingCollection) {
            if(!outstandingCollection.isEmpty()) {
                return outstandingCollection.remove(0);
            }
        }
        return null;
    }

    public static boolean removeTransactionFromOutstandingCollection(String uniqueId)
    {
        // if replicas (other than primary) call this function then it returns false since replica won't add
        // anything to the outstanding collection
        synchronized (outstandingCollection) {
            for(Transaction transaction: outstandingCollection)
            {
                if(transaction.getUnique_id().equals(uniqueId))
                {
                    outstandingCollection.remove(transaction);
                    return true;
                }
            }
        }
        return false;
    }

    public static void addTransactionToExecutedList(Transaction transaction) {
        synchronized (executedList) {
            executedList.add(transaction);
        }
        orderCounter = orderCounter + 1;
    }



}
