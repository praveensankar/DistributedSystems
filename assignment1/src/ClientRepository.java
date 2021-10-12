import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

public class ClientRepository {

  Cache cache;
  LoadBalancerInterface loadBalancer;

  // Send in null for naive implementation
  private ClientRepository(Cache cache) {
    this.cache = cache;
  }

  static ClientRepository create(Cache cache) throws Exception {
    ClientRepository cr = new ClientRepository(cache);
    cr.setLoadBalancer();
    return cr;
  }

  public TimesPlayedTask execute(TimesPlayedTask task) {

    task.setTimeStarted(System.currentTimeMillis());

    try {

      if (cache != null) {

        synchronized (cache) {
          task = cache.fetchFromCache(task); // UNCOMMENT TO FECTH FROM CACHE
        }

        System.out.println("TimesPlayedTask : music id : "+task.getMusicID()+"\t count : "+task.getResult());

      }

      if (!task.hasResult()) {
        task = getServer(task).executeQuery(task);

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

  public TimesPlayedByUserTask execute(TimesPlayedByUserTask task) {

    task.setTimeStarted(System.currentTimeMillis());

    try {

      if (cache != null) {

        synchronized (cache) {
          task = cache.fetchFromCache(task);
        }

      }

      if (!task.hasResult()) {

        task = getServer(task).executeQuery(task);

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

  public TopArtistsByUserGenreTask execute(TopArtistsByUserGenreTask task) {

    task.setTimeStarted(System.currentTimeMillis());

    try {

      if (cache != null) {

        synchronized(cache) {
          task = cache.fetchFromCache(task); // UNCOMMENT TO FECTH FROM CACHE
        }

      }

      if (!task.hasResult()) {
        task = getServer(task).executeQuery(task);

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

  public TopThreeMusicByUserTask execute(TopThreeMusicByUserTask task) {

    task.setTimeStarted(System.currentTimeMillis());

    try {
      if (cache != null) {

        synchronized(cache) {
          task = cache.fetchFromCache(task); // UNCOMMENT TO FECTH FROM CACHE
        }

      }

      if (!task.hasResult()) {

        task = getServer(task).executeQuery(task);

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

  private void setLoadBalancer() throws Exception {
    Registry registry = LocateRegistry.getRegistry();
    loadBalancer = (LoadBalancerInterface) registry.lookup("loadbalancer");
  }

  private ServerInterface getServer(Task<?> task) {
    try {
      LoadBalancerResponse r = loadBalancer.fetchServer(task.getZoneID());
      return r.serverStub;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
