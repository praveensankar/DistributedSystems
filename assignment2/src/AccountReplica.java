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
    private static boolean naive = true;
    private static double balance = 0.0;
    private static int orderCounter = 0;
    private static int outstandingCounter = 0;
    private static ArrayList<Transaction> executedList=new ArrayList<Transaction>();
    private static ArrayList<Transaction> outstandingCollection=new ArrayList<Transaction>();
    public static ArrayList<String> members = new ArrayList<>();
    private static ScheduledExecutorService executor;

    // Arguments:
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

        // Just for checkTxStatus
        if (fileName != null ) {
            replicaId = "primary";
        }

        try {
            setUpSpreadConstructs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitForAllReplicas();

        setUpScheduledExecutor(5);


//        System.out.println("After while");

        if (fileName != null ) {
            parseFileArguments(fileName);
        }

        startAcceptingUserInput();

    }

    private static void waitForAllReplicas() {

        synchronized (members) {
            while (members.size() < numberOfReplicas) {
                System.out.println("number of replicas: "+members.size());
//                System.out.println("waitForAllReplicas: Before wait.");
                try {
                    // This releases the lock associated
                    // with the object on which wait is invoked.
                    members.wait();
                } catch(Exception e) {
                    e.printStackTrace();
                }
//                System.out.println("waitForAllReplicas: After wait.");
            }
            System.out.println("number of replicas: "+members.size());

            if(balance==0)
            {
                getState();
            }
        }

    }



    /**
     *
     * The commands
     *
     */

    public static void getQuickBalance(){
        System.out.println("quick balance : " + balance);
    }

    public static void getSyncedBalance() {
        System.out.println("synched balance : " + balance);
    }

    // This assumes that outstandingCollection is only empty when they have been executed.
    // This assumption is correct as we remove from outstanding in the listener
    // only after the task has been executed.
    private static void getSyncedBalanceNaive() {
        // Todo: do the sync part
        // naive implementation: we are checking whether the outstanding collection is empty or not and running it in a
        // infinite loop. once the outstanding collection is empty then we will print the balance

        // corner case: after get synced balance is called, it blocks the current execution. so new deposits or
        // add interest commands won't be added to the outstanding collection till the get synced balance is finished
       // System.out.println("synced balance is called");

        // do {
        //     // This is the only place where order counter is changed
        //     synchronized (executedList) {
        //         if (outstandingCounter == orderCounter) {
        //             System.out.println("synced balance : " + balance);
        //             break;
        //         }
        //     }
        //
        // } while(true);

        // I think I can check if outstanding is empty. As it is just removed
        // after the execution (in the listener)
        synchronized (outstandingCollection) {

            while (!outstandingCollection.isEmpty()) {
//                System.out.println("getSyncedBalanceNaive: Before wait");
                try {
                    outstandingCollection.wait();
                } catch(Exception e) {
                    e.printStackTrace();
                }
//                System.out.println("getSyncedBalanceNaive: After wait");
            }

            System.out.println("synced balance : " + balance);
        }
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
        synchronized (executedList) {
            for (Transaction transaction : executedList) {
                System.out.println(counter + " : " + transaction.toString());
                counter++;
            }
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
            executedList.clear();
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
        // do {
        //     synchronized (outstandingCollection) {
        //         if (outstandingCollection.isEmpty()) {
        //             break;
        //         }
        //     }
        // } while (true);

        // not sure if this might lead to some troubles
        // I think not, as only one will get back the lock on the object
        // only when the lock is gotten will it check and then, if condition is
        // met, wait.
        synchronized (outstandingCollection) {
            while (!outstandingCollection.isEmpty()) {
//                System.out.println("exit: Before wait");
                try {
                    outstandingCollection.wait();
                } catch(Exception e) {
                    e.printStackTrace();
                }
//                System.out.println("exit: After wait");
            }
        }


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
            System.out.println("group members updated");
            members.notifyAll();
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

        String uniqueId = replicaId;

        if (!cmd.startsWith("getSyncedBalance") || cmd.equals("exit")) {
            uniqueId += outstandingCounter;
            outstandingCounter += 1; // only one thread changes this. (the scheduled)
        }

        Transaction transaction = createTransaction(cmd, uniqueId);

        synchronized(outstandingCollection) {
            outstandingCollection.add(transaction);
        }

        System.out.println("Adding to outstandingCollection: " + transaction.toString() );

   }


    public static void removeTransactionFromOutstandingCollection() {
        synchronized (outstandingCollection) {
            if (!outstandingCollection.isEmpty()) {
                Transaction transaction = outstandingCollection.remove(0);
                System.out.println(transaction.toString()+" is removed from the outstanding collection");
                outstandingCollection.notifyAll();
            }
        }
    }


    // Might be that several threads that change this. Not sure how the listener is done
    public static void addTransactionToExecutedList(Transaction transaction) {
        synchronized (executedList) {
            executedList.add(transaction);
            orderCounter = orderCounter + 1;
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

    // use getState as cmd and multicast the request to others
    public static  void getState()
    {
        // cmd - getState
        // uniqueId - balance
        Transaction transaction = new Transaction("getState", String.valueOf(balance));
        try {
            if(balance==0) {
                multicastTransaction(transaction, accountName);
            }
        } catch (SpreadException e) {
            e.printStackTrace();
        }
    }
    public static void multicastState()
    {
        // cmd - state
        // uniqueId - balance
        Transaction transaction = new Transaction("stateInfo", String.valueOf(balance));
        try {
            if(balance>0){
                multicastTransaction(transaction, accountName);
            }
        } catch (SpreadException e) {
            e.printStackTrace();
        }
    }
    public static void setState(double bal)
    {
        if(balance < bal) {
            balance = bal;
        }
    }


    /**
     *
     * Set up methods
     *
     */
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

    /* Sends outstanding collections to other members in the group every 'rate' seconds.
    This is done on a separate thread */
    private static void setUpScheduledExecutor(int rate) {

        Runnable sendOutstandingCollection = new Runnable() {
            public void run() {
                if(!outstandingCollection.isEmpty()){
                    System.out.println("\nSending outstanding collection\n");
                }
                multicastOutstandingCollection();
            }
        };

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(sendOutstandingCollection, 0, rate, TimeUnit.SECONDS);
    }

    /* Loops until the program is terminated. For example by exit. */
    private static void startAcceptingUserInput() {
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
                    getSyncedBalanceNaive();
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
