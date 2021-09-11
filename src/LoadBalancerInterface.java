import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote{

    // fetches the server stub from the rmi registry
    // zoneId: zone id of the client
    // return stub (ServerInterface)
    ServerInterface fetchServer(int zoneId) throws RemoteException;

}
