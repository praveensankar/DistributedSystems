import java.rmi.RemoteException;
import java.util.*;

public class TopArtistsByMusicGenreTask extends Task<String[]> {

  private static final long serialVersionUID = 14L;

  String userID;
  String genre;

  TopArtistsByMusicGenreTask(String userID, String genre, int zoneID) {
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

  @Override
  public Task<String[]> execute(Cache cache) {
    // TODO: call the correct method in cache here
    ArrayList<String> timesPlayed = cache.getTopArtistsByUserGenreInCache(this.userID, this.genre);
    if(timesPlayed==null)
      return null;
    super.setResult(timesPlayed.toArray(new String[0]));
    return this;
  }

  @Override
  public Task<String[]> execute(ServerInterface server) throws RemoteException {
    return server.getTopArtistsByMusicGenre(this);
  }

  @Override
  public String toString() {
    return "Top three artists for genre " + genre
            + " and user " + userID
            + " were " + result[0] + ", " + result[1] + ", " + result[2]
            + ". " + super.toString();
  }
}
