import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class ServerSimulator {
  public static void main(String[] args) {
      try {
          Server[] server = new Server[5];
          Registry registry = LocateRegistry.getRegistry();
          for(int i=0; i<5; i++) {
              server[i] = new Server();
              ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server[i], 0);
              registry.bind("server" + Integer.toString(i), stub);
          }
      } catch(Exception e) {
          System.err.println("Server exception: " + e.toString());
          e.printStackTrace();
      }
  }
}
