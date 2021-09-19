import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ExecutionException;

// I wonder if it is best to return a task class here?

// According to stack overflow, submit() is thread safe

// TODO: Use an alternative to System.nanoTime() as it doesn't work across VMs
public class Server implements ServerInterface {

  private ThreadPoolExecutor waitListExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
  private ThreadPoolExecutor queryExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

  private Cache cache;
  // How should I shut them down? Maybe when client is finished
  private int id;

  public Server(int id) {
    this.id = id;
  }

  public int getID() {
    return this.id;
  }

  // this setter method is used to enable cache
  // parameters:
  // cache object
  // set this in the server simulator if needed
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  // disables the cache
  public void disableCache() {
    this.setCache(null);
  }

  public Cache getCache() {
    return this.cache;
  }

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
    }

    return -1;
  }


  @Override
  public TimesPlayedTask executeQuery(TimesPlayedTask task) {

    // Unsure if should do it here or in Task
    // It should at least be done outside the queryExecutor thread.
    // To simulate the travel time, the request should not arrive before
    // the travel time is complete.
    // This will however stop the client for 80 seconds...
    // So it should be moved back into the queryExecutor thread.
    // Or actually, the client starts this in another thread
    // (and requests the waiting list in the main thread), so it can be outside
    // Can also placed this inside the clientExecutor on client side.
    // Then it will be clearer, and this method will never pause the
    // server thread if we decide to place a main method here.
    // Well actually, we need to do this here so that it won't interfere with cache

    simulateLatency(task);

    Future<TimesPlayedTask> future = queryExecutor.submit(() -> {

      // task.setTimeStarted(System.nanoTime());
      task.setTimeStarted(System.currentTimeMillis());

      int count = 0;

      try {

        if (cache != null) {
          count =  cache.getTimesPlayedFromCache(task.getMusicID());
          if(count!=0) {
            System.out.println("server : "+ id + "\t getTimesPlayed for "+task.getMusicID()+" is answered from cache. result : "+count);
          }
        }

        if(cache == null || count == 0) {
          // if the query is not in cache then read from the data set
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
          // update the cache
          // artist id is null for now. once it's parsed from the file then add it
          this.cache.addMusicToMusicProfile(task.getMusicID(), null, count);
        }

      } catch(Exception e) {
        e.printStackTrace();
      }

      task.setResult(count);
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
  public TimesPlayedByUserTask executeQuery(TimesPlayedByUserTask task) {

    simulateLatency(task);

    Future<TimesPlayedByUserTask> future = queryExecutor.submit(() -> {

      // task.setTimeStarted(System.nanoTime());
      task.setTimeStarted(System.currentTimeMillis());

      int count = 0;

      try {
        System.out.print("getTimesPlayed by user "+task.getUserID()+" , music id : "+ task.getMusicID() +"is called" );

        if (cache != null) {
            count =  this.cache.getTimesPlayedByUserFromCache(task.getMusicID(), task.getUserID());

            if(count!=0)
              System.out.println("\t result : "+ count);
            else
              System.out.println(" \tcache didn't have it");
        }

        if (cache == null || count == 0) {

          BufferedReader br = newReader();
          String line, delimiter = ",";

          while ((line = br.readLine()) != null) {
            String[] record = line.split(delimiter);
            String genre = record[record.length - 3];
            String artistId = record[record.length - 4];
            // if (record[0].equals(musicID) &&
            if (record[0].equals(task.getMusicID()) &&
                // record[record.length - 2].equals(userID)) {
                record[record.length - 2].equals(task.getUserID())) {
              // return Integer.parseInt(record[record.length - 1]);
              count += Integer.parseInt(record[record.length - 1]);

              // update the cache
              this.cache.addUserProfile(task.getUserID(),genre, task.getMusicID(), artistId, count);
            }
          }
        }
      } catch(Exception e) {
        e.printStackTrace();
      }

      task.setResult(count);

      return task;
    });

    try {
      return future.get();
    } catch(InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Here I am assuming that there is only one record per song for a specific user.
  // Apparently not the case
  @Override
  public TopThreeMusicByUserTask executeQuery(TopThreeMusicByUserTask task) {

    simulateLatency(task);

    Future<TopThreeMusicByUserTask> future = queryExecutor.submit(() -> {

      // task.setTimeStarted(System.nanoTime());
      task.setTimeStarted(System.currentTimeMillis());


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

        // br.close();

      } catch(Exception e) {
        e.printStackTrace();
      }

      task.setResult(top3);

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
  public TopArtistsByMusicGenreTask executeQuery(TopArtistsByMusicGenreTask task) {
    simulateLatency(task);

    Future<TopArtistsByMusicGenreTask> future = queryExecutor.submit(() -> {
      String[] top3 = new String[3];
      int count[] = new int[3];

      try {
        System.out.print("getTopArtistsByMusicGenre by user "+task.getUserID()+" , genre : "+ task.getGenre()
                +"is called" );

        if (cache != null) {
          ArrayList<String> topArtists =  this.cache.getTopArtistsByUserGenreInCache(task.getUserID(),task.getGenre());
          top3 = topArtists.toArray(new String[3]);

          if (top3 != null)
            System.out.println("\t result : "+ count);
          else
            System.out.println(" \tcache didn't have it");
        }

        if (top3 == null) {
          // RandomAccessFile raf = new RandomAccessFile("../data/dataset.csv", "r");
          BufferedReader br = newReader();
          String line, delimiter = ",";

          // Accumulating all the relevant values and placing them in a HashMap
          HashMap<String, Integer> artistToCount = new HashMap<>();

          while ((line = br.readLine()) != null) {
            String[] record = line.split(delimiter);
            String user = record[record.length - 2];
            String genre = record[record.length - 3];

            if (user.equals(task.getUserID()) && genre.equals(task.getGenre())) {

              // Calculating the number of artists
              int numOfArtists = (record.length % 5) + 1;

              // Getting the count of listens
              int timesPlayed = Integer.parseInt(record[record.length - 1]);

              // Adding the count to the existing count
              for (int i = 1; i <= numOfArtists; i++) {

                String artistID = record[i];
                int artistCount = artistToCount.getOrDefault(artistID, 0);
                artistToCount.put(artistID,  timesPlayed + artistCount);

              }
            }
          }

          // Performing a simple insertion sort for each entry

          for (HashMap.Entry<String, Integer> entry : artistToCount.entrySet()) {

            int timesPlayed = entry.getValue();

            int lastIndex = count.length - 1;

            if (timesPlayed > count[lastIndex]) {

              String artist = entry.getKey();

              int i = lastIndex - 1;

              while (i >= 0 && timesPlayed > count[i]) {
                count[i + 1] = count[i];
                top3[i + 1] = top3[i--];
              }

              count[i + 1] = timesPlayed;
              top3[i + 1] = artist;
            }
          }

          // Todo: update the cache
          // this.cache.addUserProfile(t.getUserID(),genre, t.getMusicID(), artistId,count);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      task.setResult(top3);

      return task;

    });

    try {
      return future.get();
    } catch(InterruptedException | ExecutionException e) {
      return null;
    }
  }


  private void simulateLatency(Task<?> task) {
    if (task.sameZone())
      try { Thread.sleep(80); } catch(Exception e) { e.printStackTrace(); }
    else
      try { Thread.sleep(170); } catch(Exception e) { e.printStackTrace(); }
  }

  // private int[] testTop3Genre(TopArtistsByMusicGenreTask task) {
  //   try {
  //     String[] top3 = new String[3];
  //     int count[] = new int[3];
  //     // RandomAccessFile raf = new RandomAccessFile("../data/dataset.csv", "r");
  //     BufferedReader br = newReader();
  //     String line, delimiter = ",";
  //
  //     // Accumulating all the relevant values and placing them in a HashMap
  //     HashMap<String, Integer> artistToCount = new HashMap<>();
  //
  //     while ((line = br.readLine()) != null) {
  //       String[] record = line.split(delimiter);
  //       String user = record[record.length - 2];
  //       String genre = record[record.length - 3];
  //
  //       System.out.println(user + ", " + genre);
  //
  //       if (user.equals(task.getUserID()) && genre.equals(task.getGenre())) {
  //
  //         System.out.println("TRUE");
  //
  //         // Calculating the number of artists
  //         int numOfArtists = (record.length % 5) + 1;
  //
  //         // Getting the count of listens
  //         int timesPlayed = Integer.parseInt(record[record.length - 1]);
  //
  //         // Adding the count to the existing count
  //         for (int i = 1; i <= numOfArtists; i++) {
  //
  //           String artistID = record[i];
  //           int artistCount = artistToCount.getOrDefault(artistID, 0);
  //           artistToCount.put(artistID,  timesPlayed + artistCount);
  //
  //         }
  //       }
  //     }
  //
  //     // Performing a simple insertion sort for each entry
  //
  //     for (HashMap.Entry<String, Integer> entry : artistToCount.entrySet()) {
  //
  //       int timesPlayed = entry.getValue();
  //
  //       int lastIndex = count.length - 1;
  //
  //       if (timesPlayed > count[lastIndex]) {
  //
  //         String artist = entry.getKey();
  //
  //         int i = lastIndex - 1;
  //
  //         while (i >= 0 && timesPlayed > count[i]) {
  //           count[i + 1] = count[i];
  //           top3[i + 1] = top3[i--];
  //         }
  //
  //         count[i + 1] = timesPlayed;
  //         top3[i + 1] = artist;
  //       }
  //     }
  //     task.setResult(top3);
  //     return count;
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //     return null;
  //   }
  //
  // }
  //
  // public static void main(String[] args) {
  //   Server s = new Server(0);
  //
  //   TopArtistsByMusicGenreTask task = new TopArtistsByMusicGenreTask("U11", "Rock", 0);
  //
  //   int[] counts = s.testTop3Genre(task);
  //
  //   System.out.println(task.toString());
  //
  //   for (int c : counts)
  //     System.out.println(c);
  //
  // }
  //
  // // DELETE BEFORE DELIVERY
  // // Trying with buffered reader. Faster than scanner according to google
  // // https://www.javatpoint.com/how-to-read-csv-file-in-java
  // void readCSVfile() {
  //
  //   String line = "";
  //   String splitBy = ",";
  //
  //   try {
  //     BufferedReader br = new BufferedReader(new FileReader("../data/dummydataset.csv"));
  //
  //     while ((line = br.readLine()) != null) {
  //       String[] record = line.split(splitBy);
  //       // 0 1 2 3 4
  //       // M A G U T
  //       // 0 1 2 3 4 5
  //       // M A A G U T
  //       String[] columnNames = {"MusicID", "ArtistID", "Genre", "UserID", "Times played"};
  //
  //       // getting the number of artists by using mod "number of columns"
  //       int numOfArtists = (record.length % 5) + 1;
  //
  //       System.out.print(columnNames[0] + ": " + record[0] + "\t");
  //
  //       System.out.print(columnNames[1] + ": ");
  //       for (int i = 1; i <= numOfArtists; i++)
  //         System.out.print(record[i] + " ");
  //
  //       System.out.print("\t");
  //
  //       for (int i = 2; i < columnNames.length; i++)
  //         System.out.print(columnNames[i] + ": " + record[i + numOfArtists - 1] + "\t");
  //
  //       System.out.println("");
  //     }
  //
  //
  //   } catch(IOException e) {
  //       e.printStackTrace();
  //   }
  //
  // }
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
