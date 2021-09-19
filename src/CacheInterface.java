import java.util.*;
public interface CacheInterface{
    public void addMusicToMusicProfile(String musicId, String artistId, int numberOfTimesPlayed);
    public void addUserProfile(String userId, String genre, String musicId, String artistId, int numberOfTimesPlayed);
    public int getTimesPlayedFromCache(String musicId);
    public int getTimesPlayedByUserFromCache(String musicId, String userId);
    public ArrayList<String> getTopArtistsByUserGenreInCache(String userId, String genre);
}