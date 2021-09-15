// I think the rmi will marshall and then unmarshall the task when it is sent
// between client and server

abstract class Task<T> {

  private long serverID;
  private long timeRequested;
  private long timeStarted;
  private long timeFinished;
  protected T result;

  public void setResult(T r) {
    result = r;
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

  public double getWaitTime() {
    return (timeStarted - timeFinished) / 1000000.0;
  }

  public double getTurnaroundTime() {
    return (timeFinished - timeRequested) / 1000000.0;
  }

  public double getExecutionTime() {
    return (timeFinished - timeStarted) / 1000000.0;
  }

  @Override
  public String toString() {
    return "(turnaround time: " + getTurnaroundTime()
            + ", execution time: " + getExecutionTime()
            + ", waiting time: " + getWaitTime()
            + ", processed by server " + serverID + ")";
  }
}
