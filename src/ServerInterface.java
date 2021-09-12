import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    String sayHello() throws RemoteException;
    int add(int a, int b) throws RemoteException;
    int getTimesPlayed(String musicID) throws RemoteException;
    int getTimesPlayedByUser(String musicID, String userID) throws RemoteException;
    String[] getTopThreeMusicByUser(String userID) throws RemoteException;
    String getTopArtistByMusicGenre(String userID, String genre) throws RemoteException;
}
