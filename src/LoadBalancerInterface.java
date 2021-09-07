import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote{
    ServerInterface fetchServer(int zoneId) throws RemoteException;
}
