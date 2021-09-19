import java.rmi.RemoteException;
import java.util.function.Supplier;

public class ClientRepository {

  // To use with cache:
  // ClientRepository repo = new ClientRepository(new Cache());

  // To use without cache:
  // ClientRepository repo = new ClientRepository(null);

  Cache cache;

  // Send in null for naive implementation
  public ClientRepository(Cache cache) {
    this.cache = cache;
  }

  // public Task<?> execute(Task<?> t, ServerInterface server) {
  //   Task<?> task = null;
  //
  //   try {
  //     if (cache != null) {
  //       //checks whether cache is added or not
  //       // if cache is added then query is sent to cache first
  //       // if cache has result then it's returned
  //       // or else query is sent to server and the result is cached in the client cache
  //       System.out.println("query is checked against client cache");
  //       task = t.execute(cache);
  //       if(task == null) {
  //         System.out.println("client cache didn't have it. so query is sent to server");
  //         task = t.execute(server);
  //
  //         if (task instanceof TimesPlayedTask) {
  //           TimesPlayedTask taskMap = (TimesPlayedTask) task;
  //           this.cache.addMusicToMusicProfile(taskMap.getMusicID(), null, (int)task.getResult());
  //           System.out.println("query is added to client cache");
  //         }
  //
  //         if (task instanceof TimesPlayedByUserTask) {
  //
  //         }
  //
  //         if(task instanceof TopArtistsByMusicGenreTask) {
  //           TopArtistsByMusicGenreTask taskMap = (TopArtistsByMusicGenreTask) task;
  //          // this.cache.addUserProfile(taskMap.getMusicID(), taskMap.getGenre(), (int)task.getResult());
  //           System.out.println("query is added to client cache");
  //         }
  //       } else {
  //         System.out.println("query is answered from client cache");
  //       }
  //     } else {
  //       task = t.execute(server);
  //     }
  //   } catch(Exception e) {
  //     return null;
  //   }
  //
  //
  //   return task;
  //
  // }

  /*

  TO DO: Implement it like this instead:


   */
  public TimesPlayedTask execute(TimesPlayedTask t, ServerInterface server) {
    TimesPlayedTask task = null;

    try {
      if (cache != null) {
        // task = t.execute(cache);
      }

      if (task == null) {
        task = server.executeQuery(t);
      }

      return task;
    } catch(Exception e) {
      return null;
    }

  }

  public TimesPlayedByUserTask execute(TimesPlayedByUserTask t, ServerInterface server) {
    TimesPlayedByUserTask task = null;

    try {
      if (cache != null) {
        // task = t.execute(cache);
      }

      if (task == null) {
        task = server.executeQuery(t);
      }

      return task;
    } catch(Exception e) {
      return null;
    }

  }

  public TopArtistsByMusicGenreTask execute(TopArtistsByMusicGenreTask t, ServerInterface server) {
    TopArtistsByMusicGenreTask task = null;


    try {
      if (cache != null) {
        // task = t.execute(cache);
      }

      if (task == null) {
        System.out.println("Calling TopArtistsByMusicGenreTask in server");
        task = server.executeQuery(t);
      }

      return task;
    } catch(Exception e) {
      return null;
    }

  }

  public TopThreeMusicByUserTask execute(TopThreeMusicByUserTask t, ServerInterface server) {
    TopThreeMusicByUserTask task = null;

    try {
      if (cache != null) {
        // task = t.execute(cache);
      }

      if (task == null) {
        task = server.executeQuery(t);
      }

      return task;
    } catch(Exception e) {
      return null;
    }

  }


}
