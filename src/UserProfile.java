import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Deque;
import java.util.Set;

class UserProfile{

  /* UserProfile contains userId and for each userId it stores the map of "genre"
  for each genre it stores the map of music profiles , times played (for 3 music profiles)

  To test the code:

  MusicProfile musicprofile = new MusicProfile("music1", "artist1");
  UserProfile userProfile = new UserProfile();
  userProfile.userId="1234";
  userProfile.musicProfileMap.put("rock", musicProfile);
  HashMap<MusicProfile, Integer> musicProfileMap = new HashMap<MusicProfile, Integer>(10);
  musicProfileMap.put(musicprofile, 10);
  userProfile.musicProfileMap.put("rock",musicProfileMap);


  HashMap<MusicProfile, Integer> musicProfileMap = userProfile.musicProfileMap.get("rock");
  for (MusicProfile mp: musicProfileMap.keySet())
  {
      String musicId = mp.musicId;
      String artistId = mp.artistId;
      System.out.println(musicId+artistId);
  }
   */
  String userId;
  //String genre
  HashMap<String, HashMap<MusicProfile, Integer>> musicProfileMap = new HashMap<String, HashMap<MusicProfile, Integer>>(3);

  int timesPlayed;
  // this queue stores the order of insertion for the genre for each user
  public Deque<String> genreOrder = new ArrayDeque<String>(3);

  public UserProfile(String userId) {
    this.userId = userId;
  }

  public Set<String> getGenres() {
    return musicProfileMap.keySet();
  }

  public void addTimesPlayed(int timesPlayed)
  {
    this.timesPlayed = timesPlayed;
  }

  public int getTimesPlayed()
  {
    return this.timesPlayed;
  }

  public void addMusic(String genre, String musicId, String artistId, int numberOfTimesPlayed) {
    // create music profile object
    MusicProfile musicProfile = new MusicProfile(musicId, artistId);

    // map the number of times played to the music player object
    HashMap<MusicProfile, Integer> musicPlayCount = new HashMap<MusicProfile, Integer>(10);
    musicPlayCount.put(musicProfile, 10);

    // add the music play count map to the genre
    musicProfileMap.put(genre,musicPlayCount);
  }
}
