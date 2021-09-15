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
    // Registry registry = null;
      try {
        Registry registry = LocateRegistry.getRegistry();
          for(int i=0; i<5; i++) {
              server[i] = new Server();
              stubs[i] = (ServerInterface) UnicastRemoteObject.exportObject(server[i], 0);
              registry.bind("server" + (i + 1), stubs[i]);
          }
          // lock.lock();
      } catch(RemoteException e) {
        System.err.println("Server exception: " + e.toString());
        unexport();
        e.printStackTrace();
        System.exit(1);
      } catch(Exception e) {
        unexport();
        e.printStackTrace();
        System.exit(1);
      } finally {
        // System.out.println("\n\nExit sever simulator\n\n");
        // done();
      }
  }

  static void unexport() {
    System.out.println("\n\nUnexporting\n\n");
    for (Server s : server)
      if (s != null)
        s.shutDownServer();


    for (ServerInterface s : stubs) {
      try { UnicastRemoteObject.unexportObject(s, false); }
      catch(Exception e) { e.printStackTrace(); }
    }
  }

  // The client does not have this instance. Or mabye I can insert into registry?
  // Wait, this is not an instance.
  public static void done() {
    try {
      unexport();
    } catch(Exception e) {

    } finally {
      // lock.unlock();
    }
  }
}
