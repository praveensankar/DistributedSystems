import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
  public int getWaitListSize() throws RemoteException;
  public <T extends Task> T executeQuery(T task) throws RemoteException;
  public int getID() throws RemoteException;
}
