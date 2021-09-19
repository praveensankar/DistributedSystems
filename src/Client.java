import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.function.Supplier;

public class Client {
  // pass the flags in the following order : -s -c
  // -s - to denote that server is added with cache
  // -c - to enable the cache in the client side

  // Implementation decision:
  // Using a cached thread pool. It will create new threads as new tasks come in
  // If a thread is available, it will use that thread instead
  // If a thread has been idle for 60 seconds, it is terminated.
  // ExecutorService is an interface
  // Can be cast to class ThreadPoolExecutor if needed
  // Only issue: timing can be harder to get right. Some tasks might reuse a
  // thread and so will have a faster execution time.
  // Alternative: initalize a fixed pool with the size of the list of the input tasks.
  // But this might create extra threads.
  static ExecutorService clientExecutor = Executors.newCachedThreadPool();

	static List<Task<?>> tasks = new ArrayList<>();

  // Will run the naive implementation
//  static Repository repository = new Repository(null);

  static Cache cache = new Cache(250);
  static ClientRepository repository;

  public static void main(String[] args) {

    String inputFile = "../input/" +  "naive_input.txt";
    String outputFile = "../output/" +  "naive_output.txt";
    if(args.length==2){
      if(args[0].equals("-s") && args[1].equals("-c")) {
        // server cache status should have been retrieved without passing it as a flag
        // but due to time constraint we are using this hack
      repository = new ClientRepository(cache);
      System.out.println("client cache is and server cache both enabled");
       inputFile = "../input/" +  "cached_input.txt";
       outputFile = "../output/" +  "server_client_cache.txt";
    }}if(args.length==1)
    {
      if(args[0].equals("-s"))
      {
        System.out.println("only server cache is enabled");
        inputFile = "../input/" +  "cached_input.txt";
        outputFile = "../output/" +  "server_cache.txt";
        repository = new ClientRepository(null);
      }
    }
    if(args.length==0)
    {
      inputFile = "../input/" +  "naive_input.txt";
      outputFile = "../output/" +  "naive_output.txt";
      repository = new ClientRepository(null);
    }
    System.out.println("\nSending the requests to server...");
    executeCommands(inputFile);
    System.out.println("...All requests have been sent to server!\n");

    System.out.println("Waiting for all requests to finish...");
    waitUntilFinished();
    System.out.println("..All requests have finished!\n");

    System.out.println("Writing to file...");
    writeToFile(outputFile);
    System.out.println("...Finished writing to file!\n");

  }

  private static void executeCommands(String file) {

    try {
      Registry registry = LocateRegistry.getRegistry();
      LoadBalancerInterface lbstub = (LoadBalancerInterface) registry.lookup("loadbalancer");

      Scanner scanner = new Scanner(new File(file));

      while (scanner.hasNextLine()) {
        String command = scanner.nextLine();
        executeCommand(command, lbstub);
      }

      scanner.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private static void waitUntilFinished() {
    try {
      // All previously submitted tasks will be executed.
      clientExecutor.shutdown();
      // Will block until all tasks have completed, or after exception, or after 300 years or so
      clientExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }


  private static void writeToFile(String outputFile) {

    //0: Counter, 1: TurnoverTime, 2: ExecutionTime, 3: WaitingTime
    int[] timesPlayed = {0,0,0,0};
    int[] timesPlayedByUser = {0,0,0,0};
    int[] topArtistsByMusicGenre = {0,0,0,0};
    int[] topThreeMusicByUser = {0,0,0,0};
    try {

      for(Task<?> t : tasks){
        if(t instanceof TimesPlayedTask){
          timesPlayed[0] += 1;
          timesPlayed[1] += t.getTurnaroundTime();
          timesPlayed[2] += t.getExecutionTime();
          timesPlayed[3] += t.getWaitTime();
        } else if (t instanceof TimesPlayedByUserTask){
          timesPlayedByUser[0] += 1;
          timesPlayedByUser[1] += t.getTurnaroundTime();
          timesPlayedByUser[2] += t.getExecutionTime();
          timesPlayedByUser[3] += t.getWaitTime();
        } else if (t instanceof TopArtistsByMusicGenreTask){
          topArtistsByMusicGenre[0] += 1;
          topArtistsByMusicGenre[1] += t.getTurnaroundTime();
          topArtistsByMusicGenre[2] += t.getExecutionTime();
          topArtistsByMusicGenre[3] += t.getWaitTime();
        } else if (t instanceof TopThreeMusicByUserTask) {
          topThreeMusicByUser[0] += 1;
          topThreeMusicByUser[1] += t.getTurnaroundTime();
          topThreeMusicByUser[2] += t.getExecutionTime();
          topThreeMusicByUser[3] += t.getWaitTime();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Unable to calculate average!");
    }

    try {

      FileWriter writer = new FileWriter(outputFile);

      for (Task<?> t : tasks) {
        writer.write(t.toString() + "\n");
      }

      writer.write("Average times for getTimesPlayed(Turnover: " + (timesPlayed[1] / timesPlayed[0]) + "ms, Execution: " + (timesPlayed[2] / timesPlayed[0]) + "ms, Waiting: " + (timesPlayed[3] / timesPlayed[0]) + "ms)" + "\n");
      writer.write("Average times for getTimesPlayedByUser(Turnover: " + (timesPlayedByUser[1] / timesPlayedByUser[0]) + "ms, Execution: " + (timesPlayedByUser[2] / timesPlayedByUser[0]) + "ms, Waiting: " + (timesPlayedByUser[3] / timesPlayedByUser[0]) + "ms)" + "\n");
      writer.write("Average times for topArtistsByMusicGenre(Turnover: " + (topArtistsByMusicGenre[1] / topArtistsByMusicGenre[0]) + "ms, Execution: " + (topArtistsByMusicGenre[2] / topArtistsByMusicGenre[0]) + "ms, Waiting: " + (topArtistsByMusicGenre[3] / topArtistsByMusicGenre[0]) + "ms)" + "\n");
      writer.write("Average times for topThreeMusicByUser(Turnover: " + (topThreeMusicByUser[1] / topThreeMusicByUser[0]) + "ms, Execution: " + (topThreeMusicByUser[2] / topThreeMusicByUser[0]) + "ms, Waiting: " + (topThreeMusicByUser[3] / topThreeMusicByUser[0]) + "ms)" + "\n");
      writer.close();

    } catch(Exception e) {
      e.printStackTrace();
      System.out.println("...Unable to write to file!");
      System.exit(-1);
    }
  }

  private static void executeCommand(String command, LoadBalancerInterface lbstub) {

    Matcher m = parse(command);

    if (!m.find())
      return;

    String method = m.group(1);
    String args1 = m.group(2);
    String args2 = m.group(3);
    int zoneID = Integer.parseInt(m.group(4));

    if (method.equals("getTimesPlayed"))
      executeTask(() -> {
        return repository.execute(new TimesPlayedTask(args1, zoneID), getServer(zoneID, lbstub));
      });

    else if (method.equals("getTimesPlayedByUser"))
      executeTask(() -> {
        return repository.execute(new TimesPlayedByUserTask(args1, args2, zoneID), getServer(zoneID, lbstub));
      });

    else if (method.equals("getTopThreeMusicByUser"))
      executeTask(() -> {
        return repository.execute(new TopThreeMusicByUserTask(args1, zoneID), getServer(zoneID, lbstub));
      });

    else if (method.equals("getTopArtistsByUserGenre"))
      executeTask(() -> {
        return repository.execute(new TopArtistsByMusicGenreTask(args1, args2, zoneID), getServer(zoneID, lbstub));
      });
  }


  private static Matcher parse(String command) {
    String pattern = "^(.*)[(]([a-zA-Z0-9]*)[,]?([a-zA-Z0-9]*)[)] Zone:([1-5])";

    // Create a pattern object
    Pattern r = Pattern.compile(pattern);

    // Create a matcher object
    return r.matcher(command);
  }

  // TODO: Use an alternative to System.nanoTime() as it doesn't work across VMs
  private static void executeTask(Supplier<Task<?>> function) {

    clientExecutor.submit(() -> {

      // Long timeRequested = System.nanoTime();
      Long timeRequested = System.currentTimeMillis();
      Task<?> t = function.get();
      // Long timeFinished = System.nanoTime();
      Long timeFinished = System.currentTimeMillis();

      t.setTimeRequested(timeRequested);
      t.setTimeFinished(timeFinished);

      // Making sure that adding to shared list is thread safe
      synchronized (tasks) {
        tasks.add(t);
      }
    });

  }

  private static ServerInterface getServer(int zoneID, LoadBalancerInterface lbstub) {
    try {
      LoadBalancerResponse response = lbstub.fetchServer(zoneID);
      ServerInterface server = response.serverStub;
      return server;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
