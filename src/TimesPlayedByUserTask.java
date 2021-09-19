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
  public Task<Integer> execute(Cache cache) {
    // TODO: call the correct method in cache here
    int timesPlayed = cache.getTimesPlayedByUserFromCache(this.musicID, this.userID);
    if(timesPlayed==0)
      return null;
    super.setResult(timesPlayed);
    return this;

  }

  @Override
  public Task<Integer> execute(ServerInterface server) throws RemoteException {
    return server.getTimesPlayedByUser(this);
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times by user " + userID
            + ". " + super.toString();

  }
}
