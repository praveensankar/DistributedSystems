import java.rmi.RemoteException;


public class TopThreeMusicByUserTask extends Task<String[]> {

  private static final long serialVersionUID = 13L;

  String userID;

  TopThreeMusicByUserTask(String userID, int zoneID) {
    super(zoneID);
    this.userID = userID;
    result = new String[3];
  }

  public String getUserID() {
    return userID;
  }

  @Override
  public TopThreeMusicByUserTask execute(Cache cache) {
    // TODO: call the correct method in cache here
    return null;
  }

  @Override
  public TopThreeMusicByUserTask execute(ServerInterface server) throws RemoteException {
    return server.executeQuery(this);
  }

  @Override
  public String toString() {
    return "Top three musics for user " + userID + " were "
            + result[0] + ", " + result[1] + ", " + result[2]
            + ". " + super.toString();
  }
}
