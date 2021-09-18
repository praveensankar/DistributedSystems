import java.rmi.RemoteException;

public class Repository {

  // To use with cache:
  // Repository repo = new Repository(new Cache());

  // To use without cache:
  // Repository repo = new Repository(null);

  Cache cache;

  // Send in null for naive implementation
  public Repository(Cache cache) {
    this.cache = cache;
  }

  // For this to work I assume that cache is returning null when not found.
  public Task<?> execute(Task<?> t, ServerInterface server) throws RemoteException {
    Task<?> task = null;

    if (cache != null) {
      task = t.execute(cache);
    }

    if (task == null) {
      task = t.execute(server);
    }

    return task;
  }

}
