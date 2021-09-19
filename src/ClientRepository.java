import java.rmi.RemoteException;
import java.util.function.Supplier;

public class ClientRepository {

  // To use with cache:
  // ClientRepository repo = new ClientRepository(new Cache());

  // To use without cache:
  // ClientRepository repo = new ClientRepository(null);

  Cache cache;

  // This should use repository interface instead

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

  // For this to work I assume that cache is returning null when not found.
  public Task<?> execute(Task<?> t, ServerInterface server) {
    try {
      Task<?> task = null;

      if (cache != null) {
        task = null; // for now

        // Don't do this:
        // task = t.execute(cache);

        // Do this:
        // same can be done with the cache here, just make sure to overload the methods:
        // task = cache.fetchStoredData(t); // <- call every method for fetchStoredData for example
      }

      // If client communicates with server repository instead
      if (task == null) {
        // task = server.executeQuery(t);
        task = t.execute(server); // bad practicve I think. Not good separatioin of concern
      }

      return task;
    } catch(Exception e) {
      return null;
    }

  }



  // // For this to work I assume that cache is returning null when not found.
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
