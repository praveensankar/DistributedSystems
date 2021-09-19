import java.rmi.RemoteException;


public class TimesPlayedByUserTask extends Task<Integer> {

  private static final long serialVersionUID = 12L;

  String musicID, userID;

  TimesPlayedByUserTask(String musicID, String userID, int zoneID) {
    super(zoneID);
    this.musicID = musicID;
    this.userID = userID;
  }

  public String getMusicID() {
    return musicID;
  }

  public String getUserID() {
    return userID;
  }

  @Override
  public TimesPlayedByUserTask execute(Cache cache) {
    // TODO: call the correct method in cache here
    return null;
  }

  @Override
  public TimesPlayedByUserTask execute(ServerInterface server) throws RemoteException {
    return server.executeQuery(this);
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times by user " + userID
            + ". " + super.toString();

  }
}
