/**
 * AccountReplica
 *
 *
 */


import spread.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AccountReplica {

    // ---------------------------------------------------
    // spread related variables
    // --------------------------------------------------
    private static int numberOfReplicas;
    public static String replicaId;
    private static int port = 0;
    private static String privateGroupName;
    private static String serverAddress;
    private static String accountName;
    private static String fileName;
    private static SpreadConnection connection;
    private static Listener listener;


    //---------------------------------------------------
    // bank account state replicated machine related variables
    //----------------------------------------------------
    private static boolean naive = false;
    private static double balance = 0.0;
    private static int orderCounter = 0;
    private static int outstandingCounter = 0;
    private static ArrayList<Transaction> executedList = new ArrayList<>();
    private static ArrayList<Transaction> outstandingCollection = new ArrayList<>();
    private static ArrayList<String> members = new ArrayList<>();
    private static ScheduledExecutorService executor;

    // Arguments:
    // <server address> <account name> <number of replicas> <file name>
    // server address - String eg:- 127.0.0.1
    // account name - String eg:- testaccount
    // number of replicas - int eg:- 3
    // filename - String eg:- testfile
    public static void main(String[] args) {

        Random random = new Random();
        replicaId = Integer.toString(random.nextInt(50));
        System.out.println("replica id : " + replicaId);

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

        if (fileName != null ) {
            parseFileArguments(fileName);
            exit();
        } else {
            startAcceptingUserInput();
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
        System.out.println("synced balance : " + balance);
    }

    // This assumes that outstandingCollection is only empty when they have been executed.
    // This assumption is correct as we remove from outstanding in the listener
    // only after the task has been executed.
    private static void getSyncedBalanceNaive() {

        System.out.println("synced balance is called");

        // I think I can check if outstanding is empty. As it is just removed
        // after the execution (in the listener)
        synchronized (outstandingCollection) {

            while (!outstandingCollection.isEmpty()) {
                System.out.println("getSyncedBalanceNaive: Before wait");
                try {
                    outstandingCollection.wait();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                System.out.println("getSyncedBalanceNaive: After wait");
            }

            System.out.println("synced balance : " + balance);
        }
    }

    //hm .. just realized that this might not be thread safe
    // NOT SURE HOW MANY THREADS IS EXECUTING FROM DUE TO LISTENER
    // If only one thread then it is thread safe
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

        synchronized (executedList) {
            int counter = orderCounter - executedList.size();
            for(Transaction transaction : executedList) {
                System.out.println(counter + " : " + transaction);
                counter++;
            }
        }

        // print the outstanding collection
        System.out.println("\noutstanding collection : ");

        synchronized (outstandingCollection) {
            for (Transaction transaction : outstandingCollection) {
                System.out.println(transaction);
            }
        }

        System.out.println("\n-----------------end history-----------------\n");
    }

    public static void checkTxStatus(String uniqueId){
        synchronized (outstandingCollection) {
            for (Transaction transaction: outstandingCollection) {
                if (transaction.getUnique_id().equals(uniqueId)){
                    System.out.println("Transaction is in outstandinCollection: " + uniqueId);
                    return;
                }
            }
        }

        synchronized (executedList) {
            for (Transaction transaction: executedList) {
                if (transaction.getUnique_id().equals(uniqueId)) {
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


    public static void sleep(int duration) {
        try {
            Thread.sleep(duration * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /* The exit method is used for exiting nicely.
    It waits until all outstanding transactions are executed before exiting
    the program. */
    public static void exit() {
        // not sure if this might lead to some troubles
        // I think not, as only one will get back the lock on the object
        // only when the lock is gotten will it check and then, if condition is
        // met, wait.
        System.out.println("EXITING");
        synchronized (outstandingCollection) {
            while (!outstandingCollection.isEmpty()) {
                System.out.println("exit: Before wait");
                try {
                    outstandingCollection.wait();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                System.out.println("exit: After wait");
            }
        }


        // listener.close();
        executor.shutdown();
        System.exit(0);

    }

    public static void updateMembers(SpreadGroup[] groups) {
        synchronized (members) {
            AccountReplica.members.clear();

            for(SpreadGroup group : groups) {
                AccountReplica.members.add(group.toString());
            }

            System.out.println("updateMembers: Notifying all (members)");
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


    public static void addTransactionToOutstandingCollection(String cmd) {

        String uniqueId = replicaId;

        if (!cmd.startsWith("getSyncedBalance")) {
            uniqueId += outstandingCounter++;
            // outstandingCounter += 1; // only one thread changes this. (the scheduled)
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
                outstandingCollection.remove(0);
                System.out.println("removeTransactionFromOutstandingCollection: Notifying all (outstandinCollection)");
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

     private static void waitForAllReplicas() {

         synchronized (members) {
             while (members.size() < numberOfReplicas) {
                 System.out.println("waitForAllReplicas: Before wait.");
                 try {
                     // This releases the lock associated
                     // with the object on which wait is invoked.
                     members.wait();
                 } catch(Exception e) {
                     e.printStackTrace();
                 }
                 System.out.println("waitForAllReplicas: After wait.");
             }
         }
     }


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

                    if (cmd.equals("getSyncedBalance")) {
                        multicastTransaction(transaction, replicaId);
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
        connection.multicast(message);
    }



    /**
     *
     * Set up methods
     *
     */
    private static void setUpSpreadConstructs() throws Exception {

        listener = new Listener();
        connection = new SpreadConnection();
        connection.add(listener);
        connection.connect(InetAddress.getByName(serverAddress), port, replicaId, false, true);

        SpreadGroup groupMulticast = new SpreadGroup();
        groupMulticast.join(connection, accountName);

        SpreadGroup groupUnicast = new SpreadGroup();
        groupUnicast.join(connection, replicaId);

    }

    /* Sends outstanding collections to other members in the group every 'rate' seconds.
    This is done on a separate thread */
    private static void setUpScheduledExecutor(int rate) {

        Runnable sendOutstandingCollection = new Runnable() {

            public void run() {
                // System.out.println("\nSending outstanding collection\n");
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
                executeCommand(input.nextLine());
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


    private static void parseFileArguments(String fileName) {

        try {

            Scanner scanner = new Scanner(new File(fileName));

            while (scanner.hasNextLine()) {
                executeCommand(scanner.nextLine());
            }

            scanner.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }


    private static void executeCommand(String cmd)  {

        if (cmd.equals("getQuickBalance")) {

            getQuickBalance();

        } else if(cmd.equals("getSyncedBalance")) {

            if (naive) {
                getSyncedBalanceNaive();
            } else {
                addTransactionToOutstandingCollection(cmd);
            }

        } else if (cmd.equals("getHistory")) {

            getHistory();

        } else if (cmd.equals("cleanHistory")) {

            cleanHistory();

        } else if (cmd.equals("memberInfo")) {

            memberInfo();

        } else if (cmd.equals("exit")) {

            exit();

        } else if (cmd.startsWith("deposit") || cmd.startsWith("addInterest")) {

            addTransactionToOutstandingCollection(cmd);

        } else if (cmd.startsWith("checkTxStatus")) {

            String uniqueId = cmd.split(" ")[1];
            checkTxStatus(uniqueId);

        } else if (cmd.startsWith("sleep")) {

            int seconds = Integer.parseInt(cmd.split(" ")[1]);
            sleep(seconds);

        }
    }

}
