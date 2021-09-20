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

  /*

  TO DO: Implement it like this instead:


   */
  public TimesPlayedTask execute(TimesPlayedTask task, ServerInterface server) {

    task.setTimeStarted(System.currentTimeMillis());

    try {

      if (cache != null) {

        synchronized (cache) {
          task = cache.fetchFromCache(task); // UNCOMMENT TO FECTH FROM CACHE
        }

      }

      if (!task.hasResult()) {
        
        task = server.executeQuery(task);

        if(cache != null) {

          synchronized (cache) {
            cache.addToCache(task);
          }

        }
      }

      return task;

    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public TimesPlayedByUserTask execute(TimesPlayedByUserTask task, ServerInterface server) {

    task.setTimeStarted(System.currentTimeMillis());

    try {

      if (cache != null) {

        synchronized (cache) {
          task = cache.fetchFromCache(task);
        }

      }

      if (!task.hasResult()) {
        task = server.executeQuery(task);

        synchronized (cache) {
          cache.addToCache(task);
        }

      }

      return task;

    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public TopArtistsByUserGenreTask execute(TopArtistsByUserGenreTask task, ServerInterface server) {

    task.setTimeStarted(System.currentTimeMillis());

    try {

      if (cache != null) {

        synchronized(cache) {
          task = cache.fetchFromCache(task); // UNCOMMENT TO FECTH FROM CACHE
        }

      }

      if (!task.hasResult()) {
        task = server.executeQuery(task);

        if (cache != null) {

          synchronized (cache) {
            cache.addToCache(task);
          }

        }
      }

      return task;

    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  public TopThreeMusicByUserTask execute(TopThreeMusicByUserTask task, ServerInterface server) {

    try {

      return server.executeQuery(task);

    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }

  }

}
