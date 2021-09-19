import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReentrantLock;

class ServerSimulator {
    // pass the flags in the following order : -c
    // -c - to enable the cache in the server side

  static ReentrantLock lock = new ReentrantLock();
  static Server[] server = new Server[5];
  static ServerInterface[] stubs = new ServerInterface[5];

  public static void main(String[] args) {
      try {
        Registry registry = LocateRegistry.getRegistry();
          for(int i=0; i<5; i++) {
              server[i] = new Server(i+1);

              // sets the cache if -c flag is passed
              // if(args.length>0) {
              //     if(args[0].equals("-c"))
              //     {
              //     server[i].setCache(new Cache(100));
              //     System.out.println("cache is added to the server : " +(i+1) );
              //     }
              // }
              //
              // // disables the cache
              // server[i].disableCache();

              stubs[i] = (ServerInterface) UnicastRemoteObject.exportObject(server[i], 0);
              registry.bind("server" + (i + 1), stubs[i]);
          }
      } catch(Exception e) {
        e.printStackTrace();
      }
  }
}
