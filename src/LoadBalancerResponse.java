import java.io.Serializable;

// constructs the response from load balancer
// contains communication delay (from client to server based on zones) simulated at load balancer
// contains the actual server stub
public class LoadBalancerResponse implements Serializable {

    public int communicationDelay;
    public ServerInterface serverStub;

    public LoadBalancerResponse(int delay, ServerInterface serverStub)
    {
        this.communicationDelay = delay;
        this.serverStub = serverStub;
    }
}
