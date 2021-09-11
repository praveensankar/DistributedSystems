import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap; // HasMmap
import java.util.Random; // Random

public class LoadBalancer implements LoadBalancerInterface {

    // server request is used to store the count of requests sent to each server
    // 5 servers are identified as 1,2,3,4,5
    HashMap<Integer,Integer> serverRequests=new HashMap<Integer,Integer>();

    // stores the waiting lists of the 5 servers
    HashMap<Integer,Integer> serverWaitingLists=new HashMap<Integer,Integer>();

    int threshold = 10;

    public LoadBalancer()
    {
        // initialize the list of requests sent to each server to 0
        // initializes the waiting list of requests of each server to 0
        for(int i=1;i<=5; i++)
        {
            this.serverRequests.put(Integer.valueOf(i),0);
            this.serverWaitingLists.put(Integer.valueOf(i),0);
        }
    }


    // increases the number of requests sent to a particular server from load balancer till now
    // serverId : id of the server ( 1,2,3,4,5)
    // return none
    public void increaseServerRequestCount(int serverId)
    {   Integer id = Integer.valueOf(serverId);
        Integer currentrequestscount = this.serverRequests.get(id);
        this.serverRequests.put(id, currentrequestscount+1);
    }

    // increases the waiting list of requests for a particular server
    // serverId : id of the server ( 1,2,3,4,5)
    // return none
    public void increaseServerWaitingList(int serverId)
    {   Integer id = Integer.valueOf(serverId);
        Integer currentwaitinglist = this.serverWaitingLists.get(id);
        this.serverWaitingLists.put(id, currentwaitinglist+1);
    }

    // fetches the number of requests sent to a particular server from load balancer till now
    // serverId : id of the server (1,2,3,4,5)
    // return the count
    public int getServerRequestCount(int serverId)
    {   Integer id = Integer.valueOf(serverId);
     Integer count =  this.serverRequests.get(id);
     return Integer.parseInt(count.toString());
    }

    // fetches the waiting list of requests for a particular server from the local cache
    // serverId : id of the server (1,2,3,4,5)
    // return the count
    public int getServerWaitingListFromCache(int serverId)
    {   Integer id = Integer.valueOf(serverId);
        Integer count =  this.serverWaitingLists.get(id);
        return Integer.parseInt(count.toString());
    }

    // fetches the waiting list of requests for a particular server from the server and updates the local cache
    // serverId : id of the server (1,2,3,4,5)
    // return none
    public void updateWaitingList(int serverId)
    {
        // Todo: Once the function call is created in the interface, call the appropriate function
        // int count = server.getWaitingList();

        // Todo: Remove this following line once the server interface has the function ready
        Random rand = new Random();
        int count = rand.nextInt(20);
        this.serverWaitingLists.put(Integer.valueOf(serverId),Integer.valueOf(count));
    }

    // checks the load of  the server
    // serverId: id of the server
    // returns the load of the server
    public int getServerLoad(int serverId)
    {
        /*
        steps:
        1 ) checks the requests sent to the particular server from local cache
        2) if it is less than 10 then return the count
        3 ) it is is more  than 10 then check the local waiting list
        4) return the count of the waiting list from the cache
         */

        int numberOfRequestsSent = this.getServerRequestCount(serverId);
        int threshold = this.threshold;
        if(numberOfRequestsSent < threshold )
            return numberOfRequestsSent;
        else
        {
            int countFromWaitingList = this.getServerWaitingListFromCache(serverId);
            return countFromWaitingList;
        }
    }

    // gets the server name based on the server id
    // serverId: id of the server
    // returns server name
    public String getServerName(int serverId)
    {
        return "server" + serverId;
    }

    // checks the load of the correponding server and if it is overloaded then checks the neighbours load
    // if both neighbours are not overloaded then choose the one with less load
    // if only one of the neighbours are not overloaded then choose that one
    // if both neighbours are overloaded then choose the current server
    // serverId : id of the current server
    // return targetServerId
    public int getServerIdBasedOnLoad(int serverId)
    {
        int targetServerId = serverId;
        // checks the load of the corresponding server
        int currentServerLoad = this.getServerLoad(serverId);

        if(currentServerLoad < threshold)
        {
            targetServerId = serverId;
        }
        else
        {
            // if the current server is overloaded the check the neighbours load
            // for server 1 , left neighbour is server 5 and right neighbour is server 2
            int neighbour1 = serverId-1;
            if(neighbour1==0)
                neighbour1=5;
            int neighbour2 = (serverId+1)%5;
            int neighbour1Load = this.getServerLoad(neighbour1);
            int neighbour2Load = this.getServerLoad(neighbour2);

            // if both neighbours are not overloaded then choose the one with less load
            // if only one of the neighbours are not overloaded then choose that one
            // if both neighbours are overloaded then choose the current server
            if(neighbour1Load < threshold && neighbour2Load < threshold)
            {
                if(neighbour1Load < neighbour2Load)
                    targetServerId = neighbour1;
                else
                    targetServerId = neighbour2;
            }
            else if(neighbour1Load < threshold && neighbour2Load >= threshold)
            {
                targetServerId = neighbour1;
            }
            else if(neighbour2Load < threshold && neighbour1Load >= threshold)
            {
                targetServerId = neighbour2;
            }
            else if(neighbour1Load >= threshold && neighbour2Load >= threshold)
            {
                targetServerId = serverId;
            }
        }
        return targetServerId;
    }

    // fetches the server stub from the rmi registry
    // zoneId: zone id of the client
    // return stub (ServerInterface)
    public ServerInterface fetchServer(int zoneId) {

        int serverId = this.getServerIdBasedOnLoad(zoneId);
        String serverName = getServerName(serverId);
        try {
            Registry registry = LocateRegistry.getRegistry();
            ServerInterface stub = (ServerInterface) registry.lookup(serverName);
            this.increaseServerRequestCount(serverId);
            this.increaseServerWaitingList(serverId);
            int numberofRequestsSent = this.getServerRequestCount(serverId);
            if(numberofRequestsSent%10 == 0)
            {
                this.updateWaitingList(serverId);
            }
            int numberOfRequestsInTheWaitingList = this.getServerWaitingListFromCache(serverId);
            System.out.println("server : "+serverId+"\t number of requests sent : " +
                    numberofRequestsSent + "\t waiting list : "+ numberOfRequestsInTheWaitingList);

            return stub;

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // creates load balancer object and adds it in the registy
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

