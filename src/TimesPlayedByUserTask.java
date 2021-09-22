
public class TimesPlayedByUserTask extends Task<Integer> {

  private static final long serialVersionUID = 12L;

  String musicID, userID;

  TimesPlayedByUserTask(String musicID, String userID, int zoneID) {
    super(zoneID);
    this.musicID = musicID;
    this.userID = userID;
    result = 0;
  }

  public boolean hasResult() {
    return result != 0;
  }

  public String getMusicID() {
    return musicID;
  }

  public String getUserID() {
    return userID;
  }

  @Override
  public TimesPlayedByUserTask execute(Database database) {
    return database.executeQuery(this);
  }

  @Override
  public TimesPlayedByUserTask execute(Cache cache) {
    return cache.fetchFromCache(this);
  }

  @Override
  public void addToCache(Cache cache) {
    cache.addToCache(this);
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times by user " + userID
            + ". " + super.toString();

  }
}
