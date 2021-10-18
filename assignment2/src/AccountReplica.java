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
    public static String replicaId = "replica1";
    private static int port = 0;
    private static String privateGroupName;
    private static String serverAddress;
    private static String accountName;
    private static String fileName;
    private static SpreadConnection connection;
    private static SpreadGroup groupMulticast;
    private static SpreadGroup groupUnicast;
    private static Listener listener;



    //---------------------------------------------------
    // bank account state replicated machine related variables
    //----------------------------------------------------
    private static boolean naive = false;
    private static double balance = 0.0;
    private static volatile int orderCounter = 0;
    private static volatile int outstandingCounter = 0;
    private static ArrayList<Transaction> executedList=new ArrayList<Transaction>();
    private static ArrayList<Transaction> outstandingCollection=new ArrayList<Transaction>();
    public static ArrayList<String> members = new ArrayList<>();
    private static ScheduledExecutorService executor;
    private static boolean finished;

    public static void main(String[] args) throws FileNotFoundException {

        Random random = new Random();
        replicaId = Integer.toString(random.nextInt(50));
        System.out.println("replica id : "+replicaId);
        parseCommandLineArguments(args);

        if (fileName != null ) {
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
                System.out.println("Sending outstanding collection");
                multicastOutstandingCollection();
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(sendOutstandingCollection, 0, 5, TimeUnit.SECONDS);

        do {
            synchronized (members) {
                if (members.size() >= numberOfReplicas) {
                    System.out.println("Members = ");
                    break;
                }
            }
        } while (true);

        System.out.println("After while");

        if (fileName != null ) {
            parseFileArguments(fileName);
        }

        Scanner input = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Please enter command: ");
                parseCommand(input.nextLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    //args:
    // <server address> <account name> <number of replicas> <file name>
    // server address - String eg:- 127.0.0.1
    // account name - String eg:- testaccount
    // number of replicas - int eg:- 3
    // filename - String eg:- testfile


    /**
     *
     * The commands
     *
     */

    public static void getQuickBalance(){
        System.out.println("quick balance : " + balance);
    }

    public static void getSyncedBalance() {
        // Todo: do the sync part
        // naive implementation: we are checking whether the outstanding collection is empty or not and running it in a
        // infinite loop. once the outstanding collection is empty then we will print the balance

        // corner case: after get synced balance is called, it blocks the current execution. so new deposits or
        // add interest commands won't be added to the outstanding collection till the get synced balance is finished
        System.out.println("synced balance is called");

        do {
            // This is the only place where order counter is changed
            synchronized (executedList) {
                if (outstandingCounter == orderCounter) {
                    System.out.println("synced balance : " + balance);
                    break;
                }
            }

        } while(true);
    }

    public static void deposit(double amount){
        balance += amount;
    }

    public static void addInterest(double percent){
        balance *= 1 + (percent/100);
    }

    public static void getHistory(){
        // print the execute list
        System.out.println("\n-----------------start history-----------------\n");
        System.out.println("executed list : ");

        int counter = orderCounter - executedList.size();
        for(Transaction transaction : executedList) {
                System.out.println(counter + " : " + transaction.toString());
                counter++;
        }
        // print the outstanding collection
        System.out.println("\noutstanding collection : ");
        for (Transaction transaction : outstandingCollection) {
                System.out.println(transaction.toString());
        }

        System.out.println("\n-----------------end history-----------------\n");
    }

    public static void checkTxStatus(String uniqueId){
        synchronized (outstandingCollection) {
            for(Transaction transaction: outstandingCollection) {
                if(transaction.getUnique_id().equals(uniqueId)){
                    System.out.println("Transaction is in outstandinCollection: " + uniqueId);
                    return;
                }
            }
        }
        synchronized (executedList) {
            for(Transaction transaction: executedList) {
                if(transaction.getUnique_id().equals(uniqueId)) {
                    System.out.println("Transaction is in executedList: " + uniqueId);
                    return;
                }
            }
        }
        System.out.println("UniqueId isn't found: " + uniqueId);
    }


    public static void cleanHistory(){
        synchronized (executedList) {
            executedList = new ArrayList<>();
            System.out.println("executedList size: " + executedList.size());
        }
    }


    public static void memberInfo(){
        synchronized (members) {
            System.out.println("Members: " + members);
        }
    }


    public static void sleep(int duration){
        try {
            Thread.sleep(duration* 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void exit(){
        do {
            synchronized (outstandingCollection) {
                if (outstandingCollection.isEmpty()) {
                    break;
                }
            }
        } while (true);

        // listener.close();
        executor.shutdown();
        System.exit(0);

    }

    public static void updateMembers(SpreadGroup[] groups){
        synchronized (members) {
            AccountReplica.members.clear();
            for(SpreadGroup group : groups) {
                AccountReplica.members.add(group.toString());
            }
        }
    }


    /**
     *
     * Transaction methods
     *
     */

    private static Transaction createTransaction(String cmd, String id) {
        return new Transaction(cmd, id);
    }


    public static void addTransactionToOutstandingCollection(String cmd){

        String uniqueId;

        if (cmd.startsWith("getSyncedBalance") || cmd.equals("exit")) {
            uniqueId = replicaId;
        } else {
            uniqueId = replicaId + outstandingCounter;
            outstandingCounter += 1; // only one thread changes this. (the scheduled)
        }

        Transaction transaction = createTransaction(cmd, uniqueId);

        synchronized(outstandingCollection) {
            outstandingCollection.add(transaction);
        }

        System.out.println("Adding to outstandingCollection: " + transaction.toString() );

   }


    public static Transaction removeTransactionFromOutstandingCollection() {
        synchronized (outstandingCollection) {
            if (!outstandingCollection.isEmpty()) {
                return outstandingCollection.remove(0);
            }
        }
        return null;
    }


    // Might be that several threads that change this. Not sure how the listener is done
    public static void addTransactionToExecutedList(Transaction transaction) {
        synchronized (executedList) {
                executedList.add(transaction);
                orderCounter = orderCounter + 1;

                if (orderCounter == outstandingCounter) {
                    // finished = true;
                }
        }
    }

    /**
     *
     * Private helper methods
     *
     */

    /**
     *
     * Message sending
     *
     */
    private static void multicastOutstandingCollection() {
        synchronized (outstandingCollection) {

            for (Transaction transaction: outstandingCollection) {
                try {
                    String cmd = transaction.getCommand();

                    if (cmd.equals("getSyncedBalance") || cmd.equals("exit")) {
                        System.out.println("cmd = " + cmd);
                        multicastTransaction(transaction, replicaId);
                        // multicastTransaction(transaction, accountName);
                    } else {
                        multicastTransaction(transaction, accountName);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void multicastTransaction(Transaction transaction, String groupName) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.addGroup(groupName);
        message.setFifo();
        message.setObject(transaction);
        // System.out.println("1 transaction : "+ transaction.toString()+" before multicasted by : "+replicaId);
        connection.multicast(message);
        // System.out.println("2 transaction : "+ transaction.toString()+" after multicasted by : "+replicaId);
    }



    /**
     * Sets up spread
     */
    // TODO: Create listener outside and close it in exit
    private static void setUpSpreadConstructs() throws SpreadException, UnknownHostException {
        listener = new Listener();
        connection = new SpreadConnection();
        connection.add(listener);
        connection.connect(InetAddress.getByName(serverAddress), port, replicaId, false, true);
        groupMulticast = new SpreadGroup();
        groupMulticast.join(connection, accountName);
        SpreadGroup groupUnicast = new SpreadGroup();
        groupUnicast.join(connection, replicaId);
    }

    /**
     *
     * Parse methods
     *
     */
    private static void parseCommandLineArguments(String[] args) {
        serverAddress = args[0];
        accountName = args[1];
        numberOfReplicas = Integer.parseInt(args[2]);

        if (args.length == 4) {
            fileName = args[3];
        }
    }


    private static void parseFileArguments(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            parseCommand(command);
            //  System.out.println("command: " + command);
        }
        scanner.close();
    }


    private static void parseCommand(String cmd)  {

        if (cmd.equals("getQuickBalance")) {
            getQuickBalance();
        } else if(cmd.equals("getSyncedBalance")) {
            // Todo: Naive implementation : execute the transactions from the outstanding collection
            if (naive) {
                    getSyncedBalance();
            } else {
                    // Advanced
                    addTransactionToOutstandingCollection(cmd);
            }

        } else if(cmd.equals("getHistory")) {
            getHistory();
        } else if(cmd.equals("cleanHistory")) {
            cleanHistory();
        } else if(cmd.equals("memberInfo")) {
            memberInfo();
        } else if(cmd.equals("exit")) {
            addTransactionToOutstandingCollection(cmd);
        } else if(cmd.startsWith("deposit") || cmd.startsWith("addInterest")) {
            //double amount = Double.parseDouble(cmd.split(" ")[1]);
            addTransactionToOutstandingCollection(cmd);
            // deposit(amount);
        } else if(cmd.startsWith("checkTxStatus")) {
            String uniqueId = cmd.split(" ")[1];
            checkTxStatus(uniqueId);
        } else if(cmd.startsWith("sleep")) {
            int duration = Integer.parseInt(cmd.split(" ")[1]);
            sleep(duration);
        }
    }

}
