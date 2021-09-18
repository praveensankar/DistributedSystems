import java.util.*;
public class Cache {



    public static class MusicProfile{
        String musicId;
        String artistId;

        public MusicProfile(String musicId, String artistId)
        {
            this.musicId = musicId;
            this.artistId = artistId;
        }
    }

    // music Profile linked hash map is used to store the cache of the music profile
    // parameters:
    // MusicProfile Class Object
    // timesPlayed

    class UserProfile{
        String userId;
        HashMap<String, HashMap<MusicProfile, Integer>> musicProfile = new HashMap<String, HashMap<MusicProfile, Integer>>(3);
    }

    // I am using hash map for to represent the music profile
    // concucurrent hash map or synchronized hash maps are not needed since only one thread will be executing the tasks
    // at the same time in the server ( another thread which runs

    public void Handle()
    {

        MusicProfile profile1 = new MusicProfile("music1", "artist1");
        MusicProfile profile2 = new MusicProfile("music2", "artist2");

        HashMap<MusicProfile, Integer> musicProfile = new HashMap<MusicProfile, Integer>(10);
        musicProfile.put(profile1, 10);
        musicProfile.put(profile2, 20);

        for (MusicProfile mp: musicProfile.keySet())
        {
            String musicId = mp.musicId;
            String artistId = mp.artistId;
            System.out.println(musicId+artistId);
        }

        UserProfile userProfile1 = new UserProfile();
        userProfile1.userId="1234";
        userProfile1.musicProfile.put("rock", musicProfile);
        musicProfile = userProfile1.musicProfile.get("rock");
        for (MusicProfile mp: musicProfile.keySet())
        {
            String musicId = mp.musicId;
            String artistId = mp.artistId;
            System.out.println(musicId+artistId);
        }


    }
    public static void main(String[] args)
    {
        Cache cache =  new Cache();
        cache.Handle();


    }

}