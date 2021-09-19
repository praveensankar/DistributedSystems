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

  // public TimesPlayedTask execute(TimesPlayedTask task, ServerInterface server) {
  //   TimesPlayedTask task = null;
  //
  //   try {
  //     if (cache != null) {
  //       task = null; //fetch from cache
  //     }
  //
  //     if (task == null) {
  //       task = server.execute(task);
  //     }
  //
  //     return task;
  //
  //   } catch(Exception e) {
  //     return null;
  //   }
  // }
  //
  // public TimesPlayedTask execute(TimesPlayedTask task, ServerInterface server) {
  //   TimesPlayedTask task = null;
  //
  //   try {
  //     if (cache != null) {
  //       task = null; //fetch from cache
  //     }
  //
  //     if (task == null) {
  //       task = server.execute(task);
  //     }
  //
  //     return task;
  //
  //   } catch(Exception e) {
  //     return null;
  //   }
  //
  // }
  //
  // public TimesPlayedTask execute(TimesPlayedTask task, ServerInterface server) {
  //   TimesPlayedTask task = null;
  //
  //   try {
  //     if (cache != null) {
  //       task = null; //fetch from cache
  //     }
  //
  //     if (task == null) {
  //       task = server.execute(task);
  //     }
  //
  //     return task;
  //
  //   } catch(Exception e) {
  //     return null;
  //   }
  //
  // }
  //
  // public TimesPlayedTask execute(TimesPlayedTask task, ServerInterface server) {
  //   TimesPlayedTask task = null;
  //
  //   try {
  //     if (cache != null) {
  //       task = null; //fetch from cache
  //     }
  //
  //     if (task == null) {
  //       task = server.execute(task);
  //     }
  //
  //     return task;
  //
  //   } catch(Exception e) {
  //     return null;
  //   }
  //
  // }
  //
  public Task<?> execute(Task<?> t, ServerInterface server) {
    Task<?> task = null;

    try {
      if (cache != null) {
        //checks whether cache is added or not
        // if cache is added then query is sent to cache first
        // if cache has result then it's returned
        // or else query is sent to server and the result is cached in the client cache
        System.out.println("query is checked against client cache");
        task = t.execute(cache);
        if(task == null) {
          System.out.println("client cache didn't have it. so query is sent to server");
          task = t.execute(server);

          if (task instanceof TimesPlayedTask) {
            TimesPlayedTask taskMap = (TimesPlayedTask) task;
            this.cache.addMusicToMusicProfile(taskMap.getMusicID(), null, (int)task.getResult());
            System.out.println("query is added to client cache");
          }

          if (task instanceof TimesPlayedByUserTask) {

          }

          if(task instanceof TopArtistsByMusicGenreTask) {
            TopArtistsByMusicGenreTask taskMap = (TopArtistsByMusicGenreTask) task;
           // this.cache.addUserProfile(taskMap.getMusicID(), taskMap.getGenre(), (int)task.getResult());
            System.out.println("query is added to client cache");
          }
        } else {
          System.out.println("query is answered from client cache");
        }
      } else {
        task=  t.execute(server);
      }
    } catch(Exception e) {
      return null;
    }


    return task;

  }

  // For this to work I assume that cache is returning null when not found.
  // public Task<?> execute(Task<?> t, ServerInterface server) {
  //   try {
  //     Task<?> task = null;
  //
  //     if (cache != null) {
  //       task = null; // for now
  //
  //       // Don't do this:
  //       // task = t.execute(cache);
  //
  //       // Do this:
  //       // same can be done with the cache here, just make sure to overload the methods:
  //       // task = cache.fetchStoredData(t); // <- call every method for fetchStoredData for example
  //     }
  //
  //     // If client communicates with server repository instead
  //     if (task == null) {
  //       // task = server.executeQuery(t);
  //       task = t.execute(server); // bad practicve I think. Not good separatioin of concern
  //     }
  //
  //     return task;
  //   } catch(Exception e) {
  //     return null;
  //   }
  //
  // }



  /*

  TO DO: Implement it like this instead:


   */
  // public TimesPlayedTask execute(TimesPlayedTask t, ServerInterface server) throws RemoteException {
  //   TimesPlayedTask task = null;
  //
  //   if (cache != null) {
  //     // task = t.execute(cache);
  //   }
  //
  //   if (task == null) {
  //     task = server.executeQuery(t);
  //   }
  //
  //   return task;
  // }
  //
  // public TimesPlayedByUserTask execute(TimesPlayedByUserTask t, ServerInterface server) throws RemoteException {
  //   TimesPlayedByUserTask task = null;
  //
  //   if (cache != null) {
  //     // task = t.execute(cache);
  //   }
  //
  //   if (task == null) {
  //     task = server.executeQuery(t);
  //   }
  //
  //   return task;
  // }
  //
  // public TopArtistsByMusicGenreTask execute(TopArtistsByMusicGenreTask t, ServerInterface server) throws RemoteException {
  //   TopArtistsByMusicGenreTask task = null;
  //
  //   if (cache != null) {
  //     // task = t.execute(cache);
  //   }
  //
  //   if (task == null) {
  //     task = server.executeQuery(t);
  //   }
  //
  //   return task;
  // }
  //
  // public TopThreeMusicByUserTask execute(TopThreeMusicByUserTask t, ServerInterface server) throws RemoteException {
  //   TopThreeMusicByUserTask task = null;
  //
  //   if (cache != null) {
  //     // task = t.execute(cache);
  //   }
  //
  //   if (task == null) {
  //     task = server.executeQuery(t);
  //   }
  //
  //   return task;
  // }


}
