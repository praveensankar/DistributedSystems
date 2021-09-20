import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
  public int getWaitListSize() throws RemoteException;
  public TimesPlayedTask executeQuery(TimesPlayedTask task) throws RemoteException;
  public TimesPlayedByUserTask executeQuery(TimesPlayedByUserTask task) throws RemoteException;
  public TopThreeMusicByUserTask executeQuery(TopThreeMusicByUserTask task) throws RemoteException;
  public TopArtistsByUserGenreTask executeQuery(TopArtistsByUserGenreTask task) throws RemoteException;
  public int getID() throws RemoteException;
}
