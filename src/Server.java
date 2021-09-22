import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ExecutionException;


public class Server implements ServerInterface {

  private ThreadPoolExecutor waitListExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
  private ThreadPoolExecutor queryExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

  private Cache cache;
  private Database database;
  private int id;

  public Server(int id) {
    this.id = id;
    database = new Database();
  }

  public int getID() {
    return id;
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
    setCache(null);
  }

  public Cache getCache() {
    return cache;
  }

  /**
   * Remote methods
   */


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
  public <T extends Task> T executeQuery(T task) {

    simulateLatency(task);

    Future<T> future = queryExecutor.submit(() -> {

      task.setTimeStarted(System.currentTimeMillis());


      if (cache != null) {
        task.execute(cache);
      }

      if (!task.hasResult()) {
        task.execute(database);

        if (cache != null) {
          task.addToCache(cache);
        }
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

  private void simulateLatency(Task<?> task) {

    task.setServerID(id);

    if (task.sameZone())
      try { Thread.sleep(80); } catch(Exception e) { e.printStackTrace(); }
    else
      try { Thread.sleep(170); } catch(Exception e) { e.printStackTrace(); }

  }

}
