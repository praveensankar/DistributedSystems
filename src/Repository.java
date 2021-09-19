import java.rmi.RemoteException;

public class Repository {

  // To use with cache:
  // Repository repo = new Repository(new Cache(250));

  // To use without cache:
  // Repository repo = new Repository(null);

  private Cache cache;

  // Send in null for naive implementation
  public Repository(Cache cache) {
    this.cache = cache;
  }

//  public void setCache(Cache cache)
//  {
//    this.cache = cache;
//  }
//
//  public void disableCache()
//  {
//    this.cache = null;
//  }
//
//  public Cache createNewCache()
//  {
//    return new Cache(250);
//  }
  // For this to work I assume that cache is returning null when not found.
  public Task<?> execute(Task<?> t, ServerInterface server) throws RemoteException {
    Task<?> task = null;
    long timeStarted = System.nanoTime();
    if (cache != null) {
      //checks whether cache is added or not
      // if cache is added then query is sent to cache first
      // if cache has result then it's returned
      // or else query is sent to server and the result is cached in the client cache

      String queryName="";
      if(t instanceof TimesPlayedTask)
        queryName = "getTimesPlayed";
      if (t instanceof TimesPlayedByUserTask) {
        queryName = "getTimesPlayedByUser";
      }
      if(t instanceof TopArtistsByMusicGenreTask) {
        queryName = "getTopArtistsByMusicGenre";
      }

      if(t instanceof TopArtistsByMusicGenreTask) {
      queryName = "getTopThreeMusicByUser";
      }
      System.out.print(queryName+" is checked against client cache. ");
      task = t.execute(cache);
      if(task == null)
      {
        System.out.println("client cache didn't have it. so query is sent to server");
        task = t.execute(server);
        if (t instanceof TimesPlayedTask) {
          TimesPlayedTask taskMap = (TimesPlayedTask) task;
          this.cache.addMusicToMusicProfile(taskMap.getMusicID(), null, (int)task.getResult());
          System.out.println("query is added to client cache");
        }
        if (t instanceof TimesPlayedByUserTask) {

        }
        if(t instanceof TopArtistsByMusicGenreTask)
        {
          TopArtistsByMusicGenreTask taskMap = (TopArtistsByMusicGenreTask) task;
         // this.cache.addUserProfile(taskMap.getMusicID(), taskMap.getGenre(), (int)task.getResult());
          System.out.println("query is added to client cache");
        }
      }
      else
      {
        System.out.println("query is answered from client cache");
      }
      long timeEnded = System.nanoTime();
      task.setTimeStarted(timeStarted);
      task.setTimeFinished(timeEnded);
      return task;
    }

    if (cache== null || task == null) {
      System.out.println("query is sent to server");
      task = t.execute(server);
    }

    return task;
  }

}
