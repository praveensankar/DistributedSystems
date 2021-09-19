import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ExecutionException;

// I wonder if it is best to return a task class here?

// According to stack overflow, submit() is thread safe
public class Server implements ServerInterface {

  private ThreadPoolExecutor waitListExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
  private ThreadPoolExecutor queryExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

  // How should I shut them down? Maybe when client is finished
  private int serverId;

  public Server(int id)
  {
    this.serverId = id;
  }

  public int getServerId()
  {
    return this.serverId;
  }

  /**
   * Local methods
   */

  public void shutDownServer() {
    System.out.println("Shutting down pools");
    waitListExecutor.shutdownNow();
    queryExecutor.shutdownNow();
  }

  private BufferedReader newReader() throws IOException {
    return new BufferedReader(new FileReader("../data/dataset.csv"));
  }

  /**
   * Remote methods
   */

   // using synchronized here as these methods will not be called on excessively.
   // removing synchronized as only one thread will access at a time (the client thread?)
   // unsure of how rmi handles remote invocations
  public int getWaitListSize() {

    Future<Integer> future = waitListExecutor.submit(() -> {
      return queryExecutor.getQueue().size();
    });

    try {
      // blocking call
      return future.get();
    } catch(InterruptedException | ExecutionException e) {
      e.printStackTrace();
    return -1;
    }
  }


  @Override
  public TimesPlayedTask getTimesPlayed(TimesPlayedTask task) {

    // Unsure if should do it here or in Task
    task.setTimeRequested(System.nanoTime());

    // Unsure if should do it here or in Task
    // It should at least be done outside the queryExecutor thread.
    // To simulate the travel time, the request should not arrive before
    // the travel time is complete.
    // This will however stop the client for 80 seconds...
    // So it should be moved back into the queryExecutor thread.
    // Or actually, the client starts this in another thread
    // (and requests the waiting list in the main thread), so it can be outside
    if (!task.sameZone()) try {
      Thread.sleep(80);
    } catch(Exception e) {
      e.printStackTrace();
    }

    Future<TimesPlayedTask> future = queryExecutor.submit(() -> {

      task.setTimeStarted(System.nanoTime());

      int count = 0;

      try {

        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);

          // maybe could be replaced with an abstract class and functions to decrease
          // code duplication
          if (record[0].equals(task.getMusicID())) {
            // return Integer.parseInt(record[record.length - 1]);
            count += Integer.parseInt(record[record.length - 1]);
          }
        }

      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with parsing!\n");
        e.printStackTrace();
      }

      task.setResult(count);
      // Unsure if should do it here or in Task
      task.setTimeFinished(System.nanoTime());

      // I guess it is not completely necessary to return the task
      return task;
    });

    try {
      return future.get();
    } catch(InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return null;
    }
  }

  // TO DO: set timings
  // Question: is there several records for same musicID and userID??
  // If so, this needs to be modified. Have now modified it. But not sure if necessary.
  // If this is the case, then the top three will be more complicated
  @Override
  // public Task getTimesPlayedByUser(String musicID, String userID) {
  public TimesPlayedByUserTask getTimesPlayedByUser(TimesPlayedByUserTask t) {
    t.setTimeRequested(System.nanoTime());

    if (!t.sameZone()) try {
      Thread.sleep(80);
    } catch(Exception e) {
      e.printStackTrace();
    }

    Future<TimesPlayedByUserTask> future = queryExecutor.submit(() -> {

      t.setTimeStarted(System.nanoTime());

      int count = 0;
      try {
        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);

          // if (record[0].equals(musicID) &&
          if (record[0].equals(t.getMusicID()) &&
              // record[record.length - 2].equals(userID)) {
              record[record.length - 2].equals(t.getUserID())) {
            // return Integer.parseInt(record[record.length - 1]);
            count += Integer.parseInt(record[record.length - 1]);
          }
        }
      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with the parsing!");
        e.printStackTrace();
      }

      // Task<Integer> task = new TimesPlayedByUserTask(musicID, userID, 111);

      t.setResult(count);
      t.setTimeFinished(System.nanoTime());

      return t;
    });

    try {
      return future.get();
    } catch(InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Here I am assuming that there is only one record per song for a specific user.
  @Override
  public TopThreeMusicByUserTask getTopThreeMusicByUser(TopThreeMusicByUserTask task) {
    task.setTimeRequested(System.nanoTime());

    if (!task.sameZone()) try {
      Thread.sleep(80);
    } catch(Exception e) {
      e.printStackTrace();
    }

    Future<TopThreeMusicByUserTask> future = queryExecutor.submit(() -> {

      task.setTimeStarted(System.nanoTime());

      String[] top3 = new String[3];
      int[] count = new int[3];

      try {
        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);
          String user = record[record.length - 2];
          int timesPlayed = Integer.parseInt(record[record.length - 1]);

          if (user.equals(task.getUserID()) && timesPlayed > count[2]) {

            int i = 1;
            while (i >= 0 && timesPlayed > count[i]) {
              count[i + 1] = count[i];
              top3[i + 1] = top3[i--];
            }

            count[i + 1] = timesPlayed;
            top3[i + 1] = record[0];
          }
        }
      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with the parsing!\n");
        e.printStackTrace();
      }

      task.setResult(top3);

      task.setTimeFinished(System.nanoTime());

      return task;

    });

    try {
      return future.get();
    } catch(InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public TopArtistsByMusicGenreTask getTopArtistsByMusicGenre(TopArtistsByMusicGenreTask task) {
    task.setTimeRequested(System.nanoTime());

    if (!task.sameZone()) try {
      Thread.sleep(80);
    } catch(Exception e) {
      e.printStackTrace();
    }

    task.setTimeStarted(System.nanoTime());

    String[] top3 = new String[3];
    int count[] = new int[3];

    try {
      BufferedReader br = newReader();
      String line, delimiter = ",";

      while ((line = br.readLine()) != null) {
        String[] record = line.split(delimiter);
        String user = record[record.length - 2];
        String genre = record[record.length - 3];

        if (user.equals(task.getUserID()) &&
            genre.equals(task.getGenre())) {
          // TODO
          // run through n * n.
          // Do this do save space
        }
      }

    } catch(IOException e) {
      e.printStackTrace();
    } catch (NumberFormatException e) {
      System.err.println("Logical error: Something went wrong with the parsing!\n");
      e.printStackTrace();
    }

    task.setTimeFinished(System.nanoTime());
    // return top3;
    return task;
  }
//
//   // DELETE BEFORE DELIVERY
//   // Trying with buffered reader. Faster than scanner according to google
//   // https://www.javatpoint.com/how-to-read-csv-file-in-java
//   void readCSVfile() {
//
//     String line = "";
//     String splitBy = ",";
//
//     try {
//       BufferedReader br = new BufferedReader(new FileReader("../data/dummydataset.csv"));
//
//       while ((line = br.readLine()) != null) {
//         String[] record = line.split(splitBy);
//         // 0 1 2 3 4
//         // M A G U T
//         // 0 1 2 3 4 5
//         // M A A G U T
//         String[] columnNames = {"MusicID", "ArtistID", "Genre", "UserID", "Times played"};
//
//         // getting the number of artists by using mod "number of columns"
//         int numOfArtists = (record.length % 5) + 1;
//
//         System.out.print(columnNames[0] + ": " + record[0] + "\t");
//
//         System.out.print(columnNames[1] + ": ");
//         for (int i = 1; i <= numOfArtists; i++)
//           System.out.print(record[i] + " ");
//
//         System.out.print("\t");
//
//         for (int i = 2; i < columnNames.length; i++)
//           System.out.print(columnNames[i] + ": " + record[i + numOfArtists - 1] + "\t");
//
//         System.out.println("");
//       }
//
//
//     } catch(IOException e) {
//         e.printStackTrace();
//     }
//
//   }
//
//   // DELETE BEFORE DELIVERY
//   public static void main(String[] args) {
//
//     Server s = new Server();
//
//     // s.readCSVfile();
//
//     System.out.println("\nDummy dataset");
//
//     // Dummy dataset
//     System.out.println("\nTimes played");
//     System.out.println("M9: " + s.getTimesPlayed("M9"));
//     System.out.println("M1: " + s.getTimesPlayed("M1"));
//     System.out.println("M14: " + s.getTimesPlayed("M14"));
//
//     System.out.println("\nTimes played by user");
//     System.out.println("M1 and U1: " + s.getTimesPlayedByUser("M1", "U1"));
//     System.out.println("M1 and U2: " + s.getTimesPlayedByUser("M1", "U2"));
//     System.out.println("M1 and U3: " + s.getTimesPlayedByUser("M1", "U3"));
//     System.out.println("M10 and U4: " + s.getTimesPlayedByUser("M10", "U4"));
//
//     System.out.println("\nTop 3 music");
//     String user = "U2";
//     String[] top3 = s.getTopThreeMusicByUser(user);
//     System.out.print(user + ": ");
//
//     for (String m : top3)
//       System.out.print(m + " ");
//
//     System.out.println("\n\nReal dataset");
//
//
//     // Real dataset
//     System.out.println("\nTimes played");
//     System.out.println("MZnK007OYs: " + s.getTimesPlayed("MZnK007OYs"));
//     System.out.println("MK3r9RF0Fz: " + s.getTimesPlayed("MK3r9RF0Fz"));
//     System.out.println("Mnni7iWPl2: " + s.getTimesPlayed("Mnni7iWPl2"));
//
//     System.out.println("\nTimes played by user");
//     System.out.println("MZnK007OYs and UQ9lO8EhXd: " + s.getTimesPlayedByUser("MZnK007OYs", "UQ9lO8EhXd"));
//     System.out.println("MZnK007OYs and Uo59ASIkor: " + s.getTimesPlayedByUser("MZnK007OYs", "Uo59ASIkor"));
//     System.out.println("MK3r9RF0Fz and U1hn1abhsA: " + s.getTimesPlayedByUser("MK3r9RF0Fz", "U1hn1abhsA"));
//     System.out.println("MeD0M4CGzg and Url3aJX8dX: " + s.getTimesPlayedByUser("MeD0M4CGzg", "Url3aJX8dX"));
//
//     System.out.println("\nTop 3 music");
//     user = "U5etSKm4EW";
//     top3 = s.getTopThreeMusicByUser(user);
//     System.out.println(user + ": ");
//
//     for (String m : top3)
//       System.out.println(m + ": " + s.getTimesPlayedByUser(m, user));
//
//     System.out.println("");
//   }
}
