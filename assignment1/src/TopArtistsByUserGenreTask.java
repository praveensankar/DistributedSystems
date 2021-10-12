
public class TopArtistsByUserGenreTask extends Task<String[]> {

  private static final long serialVersionUID = 14L;

  String userID;
  String genre;

  TopArtistsByUserGenreTask(String userID, String genre, int zoneID) {
    super(zoneID);
    this.userID = userID;
    this.genre = genre;
    result = new String[3];
  }

  public String getUserID() {
    return userID;
  }

  public String getGenre() {
    return genre;
  }

  public boolean hasResult() {
    for (String s : result)
      if (s != null)
        return true;

    return false;
  }

  @Override
  public TopArtistsByUserGenreTask execute(Database database) {
    return database.executeQuery(this);
  }

  @Override
  public TopArtistsByUserGenreTask execute(Cache cache) {
    return cache.fetchFromCache(this);
  }

  @Override
  public void addToCache(Cache cache) {
    cache.addToCache(this);
  }

  @Override
  public String toString() {
    return "Top three artists for genre " + genre
            + " and user " + userID
            + " were " + result[0] + ", " + result[1] + ", " + result[2]
            + ". " + super.toString();
  }
}
