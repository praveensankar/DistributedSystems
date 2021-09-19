import java.rmi.RemoteException;
import java.io.Serializable;

// An object must be serializable for it to be sent over RMI.
// RMI will then be able to marshall and unmarshall the object.
// It's class must also be public.

public abstract class Task<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private int zoneID;
  private int serverID;
  private long timeRequested;
  private long timeStarted;
  private long timeFinished;
  protected T result;

  Task(int zoneID) {
    this.zoneID = zoneID;
  }

  public boolean sameZone() {
    return zoneID == serverID;
  }

  public void setResult(T r) {
    result = r;
  }

  public T getResult() {
    return result;
  }

  public void setServerID(int id) {
    serverID = id;
  }

  public void setTimeRequested(long time) {
    timeRequested = time;
  }

  public void setTimeStarted(long time) {
    timeStarted = time;
  }

  public void setTimeFinished(long time) {
    timeFinished = time;
  }

  public int getZoneID() {
    return zoneID;
  }

  public double getWaitTime() {
    return (timeStarted - timeRequested); // / 1000000.0;
  }

  public double getTurnaroundTime() {
    return (timeFinished - timeRequested); // / 1000000.0;
  }

  public double getExecutionTime() {
    return (timeFinished - timeStarted); // / 1000000.0;
  }

  // public abstract Task<T> execute(Cache cache);
  // public abstract Task<T> execute(ServerInterface server) throws RemoteException;

  @Override
  public String toString() {
    return "(turnaround time: " + getTurnaroundTime()
            + ", execution time: " + getExecutionTime()
            + ", waiting time: " + getWaitTime()
            + ", processed by server " + serverID + ")";
  }
}
