import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
  public int getWaitListSize() throws RemoteException;
  public TimesPlayedTask getTimesPlayed(TimesPlayedTask task) throws RemoteException;
  public TimesPlayedByUserTask getTimesPlayedByUser(TimesPlayedByUserTask task) throws RemoteException;
  public TopThreeMusicByUserTask getTopThreeMusicByUser(TopThreeMusicByUserTask task) throws RemoteException;
  public TopArtistsByMusicGenreTask getTopArtistsByMusicGenre(TopArtistsByMusicGenreTask task) throws RemoteException;
  public int getServerId() throws java.rmi.RemoteException;
}
