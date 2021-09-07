import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            LoadBalancerInterface lbstub = (LoadBalancerInterface) registry.lookup("loadbalancer");
            ServerInterface server = lbstub.fetchServer(1);
            String response = server.sayHello();
            System.out.println(response);
            int n1 = 10, n2 = 15;
            System.out.println(n1 + "+" + n2 + "=" + server.add(n1, n2));

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
