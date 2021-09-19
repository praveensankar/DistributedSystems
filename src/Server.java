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
  private Database database;

  // How should I shut them down? Maybe when client is finished
  private int id;

  public Server(int id) {
    this.id = id;
    database = new Database();
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

      // TimesPlayedTask t = null;
      // task.setTimeStarted(System.nanoTime());
      task.setTimeStarted(System.currentTimeMillis());

      if (cache != null) {
        cache.fetchFromCache(task);
        int count = (int)task.getResult();
        if(count !=0)
        {
          System.out.println("server : "+ id + "\t getTimesPlayed for "+task.getMusicID()+" is answered from cache. result : "+count);
          return task;
        }
        if(count==0)
        {
          // cache didn't have the data so fetch it from server and update cache
          database.executeQuery(task);
          String artistId = database.getMusicProfile(task.getMusicID()).artistId;
          cache.addMusicToMusicProfile(task.getMusicID(), artistId, (int)task.getResult());
        }
      }
      if(cache == null) {
        database.executeQuery(task);
      }

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

      System.out.print("getTimesPlayed by user "+task.getUserID()+" , music id : "+ task.getMusicID() +"is called" );

      if (cache != null) {
        cache.fetchFromCache(task);
        int count = (int)task.getResult();
        if(count !=0)
        {
          System.out.println(" It is answered from cache. result : "+count);
          return task;
        }
        if(count==0)
        {
          // cache didn't have the data so fetch it from server and update cache
          database.executeQuery(task);
          UserProfile userProfile = database.getUserProfile(task.getUserID(), task.getMusicID());
          String userId = task.getUserID();
          String genre  = userProfile.getGenres().iterator().next();
          String musicId = " ";
          String artistId = "";
          int numberOfTimesPlayed = 0;
          HashMap<MusicProfile, Integer> musicProfileMap = userProfile.musicProfileMap.get(genre);
          for (MusicProfile mp: musicProfileMap.keySet())
          {
            musicId = mp.musicId;
            artistId = mp.artistId;
            numberOfTimesPlayed = musicProfileMap.get(mp);
            break;
          }
          cache.addUserProfile(userId,genre,musicId,artistId, numberOfTimesPlayed);
        }
      }
      if(cache == null) {
        database.executeQuery(task);
      }


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

      database.executeQuery(task);

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

      task.setTimeStarted(System.currentTimeMillis());

      // System.out.print("getTopArtistsByMusicGenre by user "+task.getUserID()+" , genre : "+ task.getGenre()
      //         +"is called" );

      if (cache != null) {
         cache.fetchFromCache(task);
         String[] top3 = (String[]) task.getResult();
        if (top3[0]!=null)
          System.out.println("\t result : "+ top3[0]+"\t"+top3[1]+"\t"+top3[2]);
        else {
          System.out.println(" \tcache didn't have it");
          database.executeQuery(task);
        }
      }

      if (cache == null) {
        database.executeQuery(task);
        // Todo: update the cache
        // BUT ONLY IF NOT NULL!!
        // if (cache != null)
        // cache.addUserProfile(t.getUserID(),genre, t.getMusicID(), artistId,count);
      }
      System.out.println("top 3 artists : "+task.getResult()[0]+ "\t"+ task.getResult()[1] + "\t" + task.getResult()[2]);
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

}
