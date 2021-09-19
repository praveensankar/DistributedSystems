public class MusicProfile{
    /* MusicProfile stores the music Id and artist Id

    To test the code:
    MusicProfile profile1 = new MusicProfile("music1", "artist1");
     */
    String musicId;
    String artistId;

    public MusicProfile(String musicId, String artistId) {
        this.musicId = musicId;
        this.artistId = artistId;
    }

    @Override
    public String toString() {
      return "MusicProfile(" + musicId + ", " + artistId + ")";
    }
}
