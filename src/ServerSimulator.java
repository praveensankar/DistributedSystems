import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReentrantLock;

class ServerSimulator {

  static ReentrantLock lock = new ReentrantLock();
  static Server[] server = new Server[5];
  static ServerInterface[] stubs = new ServerInterface[5];

  public static void main(String[] args) {
      try {
        Registry registry = LocateRegistry.getRegistry();
          for(int i=0; i<5; i++) {
              server[i] = new Server();
              stubs[i] = (ServerInterface) UnicastRemoteObject.exportObject(server[i], 0);
              registry.bind("server" + (i + 1), stubs[i]);
          }
      } catch(Exception e) {
        e.printStackTrace();
      }
  }
}
