import java.util.*;
public class Cache {


    public static class MusicProfile{
        /* MusicProfile stores the music Id and artist Id

        To test the code:

        MusicProfile profile1 = new MusicProfile("music1", "artist1");
         */
        String musicId;
        String artistId;

        public MusicProfile(String musicId, String artistId)
        {
            this.musicId = musicId;
            this.artistId = artistId;
        }
    }

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
        HashMap<String, HashMap<MusicProfile, Integer>> musicProfileMap = new HashMap<String, HashMap<MusicProfile, Integer>>(3);

        public UserProfile(String userId)
        {
            // this constructor takes only userId
            this.userId = userId;
        }
    }

    private int musicIdCapacity = 10;
    private int userIdCapacity = 10;

    // musicProfiles hash map is used to contain music Profile object as key and it stores the number of times it played as value
    private LinkedHashMap<MusicProfile, Integer> musicProfiles;
    private Queue<UserProfile> userProfiles;

    public Cache()
    {
        // initializes the MusicProfiles and UserProfiles as Queue with the given capacity
        // queue should handle removing the old entries if it gets overflowed
        this.musicProfiles = new LinkedHashMap<MusicProfile, Integer>(this.musicIdCapacity);
        this.userProfiles = new ArrayDeque<UserProfile>(this.userIdCapacity);
    }



    public void Handle()
    {

        this.musicProfiles.put(new MusicProfile("music1", "artist1"), 10);
        this.musicProfiles.put(new MusicProfile("music2", "artist2"), 20);
        this.userProfiles.add(new UserProfile("user1"));

        for(UserProfile u:userProfiles){
            String userId = u.userId;
            if(userId.equals("user1"))
            {
                MusicProfile profile1 = new MusicProfile("music1", "artist1");
                HashMap<MusicProfile, Integer> musicProfile = new HashMap<MusicProfile, Integer>(10);
                musicProfile.put(profile1, 10);
                u.musicProfileMap.put("rock",musicProfile);
            }
        }

        for(UserProfile u:userProfiles){
            String userId = u.userId;
            if(userId.equals("user1"))
            {
                HashMap<MusicProfile, Integer> musicProfile = u.musicProfileMap.get("rock");
                for (MusicProfile mp: musicProfile.keySet())
                {
                    String musicId = mp.musicId;
                    String artistId = mp.artistId;
                    System.out.println("music id : "+musicId+"\t artist id : "+artistId);
                }
            }
        }



    }
    public static void main(String[] args)
    {
        Cache cache =  new Cache();
        cache.Handle();


    }

}