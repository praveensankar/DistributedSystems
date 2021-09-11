import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server implements ServerInterface{
    public String sayHello() {
        return "Hello, world!";
    }

    public int add(int a, int b)
    {
        return a+b;
    }

    public static void main(String[] args)
    {
        try{
            Server[] server = new Server[5];
            Registry registry = LocateRegistry.getRegistry();
            for(int i=0; i<5; i++)
            {
                server[i]=new Server();
                ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server[i], 0);
                registry.bind("server"+Integer.toString(i+1), stub);
            }
        }
        catch(Exception e)
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
