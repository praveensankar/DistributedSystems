import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoadBalancer implements LoadBalancerInterface {

    public ServerInterface fetchServer(int zoneId) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            ServerInterface stub = (ServerInterface) registry.lookup("server1");
            return stub;
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {

            LoadBalancer lb = new LoadBalancer();
            Registry registry = LocateRegistry.getRegistry();
            LoadBalancerInterface lbstub = (LoadBalancerInterface) UnicastRemoteObject.exportObject(lb, 0);
            registry.bind("loadbalancer", lbstub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

