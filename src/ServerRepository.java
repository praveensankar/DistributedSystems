import java.rmi.RemoteException;

public class ServerRepository {

  // To use with cache:
  // ServerRepository repo = new ServerRepository(new Cache());

  // To use without cache:
  // ServerRepository repo = new ServerRepository(null);

  Cache cache;

  // Send in null for naive implementation
  public ServerRepository(Cache cache) {
    this.cache = cache;
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
