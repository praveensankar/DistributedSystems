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
        //String genre
        HashMap<String, HashMap<MusicProfile, Integer>> musicProfileMap = new HashMap<String, HashMap<MusicProfile, Integer>>(3);

        // this queue stores the order of insertion for the genre for each user
        public Deque<String> genreOrder = new ArrayDeque<String>(3);

        public UserProfile(String userId)
        {
            // this constructor takes only userId
            this.userId = userId;

        }

        public Set<String> getGenres()
        {
            return musicProfileMap.keySet();
        }

        public void addMusic(String genre, String musicId, String artistId, int numberOfTimesPlayed)
        {
            // create music profile object
            MusicProfile musicProfile = new MusicProfile(musicId, artistId);

            // map the number of times played to the music player object
            HashMap<MusicProfile, Integer> musicPlayCount = new HashMap<MusicProfile, Integer>(10);
            musicPlayCount.put(musicProfile, 10);

            // add the music play count map to the genre
            this.musicProfileMap.put(genre,musicPlayCount);

        }
    }

    private int musicIdCapacity = 3;
    private int userIdCapacity = 100;

    // musicProfiles hash map is used to contain music Profile object as key and it stores the number of times it played as value
    private LinkedHashMap<MusicProfile, Integer> musicProfiles;
    private Queue<UserProfile> userProfiles;
    public int count;
    public Cache(int musicIdCapacity)
    {
        // if it is client cache then pass 250
        // it if is server cache then pass 100
        this.musicIdCapacity = musicIdCapacity;
        this.musicProfiles = new LinkedHashMap<MusicProfile, Integer>(this.musicIdCapacity) {
            protected boolean removeEldestEntry(Map.Entry<MusicProfile, Integer> eldest) {
                return size() > musicIdCapacity;
            }
        };
        this.userProfiles = new ArrayDeque<UserProfile>(this.userIdCapacity);
        this.count = 0;
    }
    public int getMusicProfileCacheSize()
    {
        return this.musicProfiles.size();
    }

    public void addMusicToMusicProfile(String musicId, String artistId, int numberOfTimesPlayed)
    {
    /*
    adds new music profile to the cache
    if the cache is full then it removes the old entry and adds the new entry automatically

    parameters:
    musicId, artistId, NumberofTimesPlayed
     */

     // add the new entry
        MusicProfile musicprofile = new MusicProfile(musicId, artistId);
        this.musicProfiles.put(musicprofile, numberOfTimesPlayed);
    }

    public void addUserProfile(String userId, String genre, String musicId, String artistId, int numberOfTimesPlayed)
    {
            /*
            steps:
            1) check whether userId exists or not
                A) if it exists then check whether the genre exists for the user or not
                    a) if it exists
                        i ) check whether the music profile for the genre is full or not
                            1) if full then remove the oldest music profile and add the new one
                            2) if not then add the new one
                     b) if it doesn't exist then
                        1) check whether already 3 genres are present or not if it does then remove the oldest
                        2) add the music profile and genre to the musicProfileMap
                B) if it doesn't exist then check whether userId Capacity is reached or not
                    a) if it is full then remove the oldest entry
                    b) add the user profile
             */

        boolean userIdExistFlag = false;
        // step 1 : checking whether user id exists or not
        for (UserProfile userProfile: this.userProfiles) {
            // step 1.A : user id exists
            if (userProfile.userId.equals(userId)) {
                userIdExistFlag = true;

                // step 1.A.a: User exists

                // Step 1.A.a.i: check whether the music profile has 3 genres or not
                Set<String> genres = userProfile.getGenres();
                if(genres.contains(genre))
                {
                    HashMap<MusicProfile, Integer> musicProfileMap = userProfile.musicProfileMap.get(genre);

                    // check if it has 3 music profiles or not
                    if(musicProfileMap.size() == 3)
                    {
                        // if it has 3 elements then remove the first element mapped
                        for (MusicProfile mp: musicProfileMap.keySet())
                        {
                            musicProfileMap.remove(mp);
                            break;
                        }
                    }

                    // add the music profile
                    MusicProfile musicProfile = new MusicProfile(musicId, artistId);
                    musicProfileMap.put(musicProfile, numberOfTimesPlayed);

                    // add the genre order to the genreOrderQueue
                    userProfile.genreOrder.add(genre);
                    break;
                }

                // Step 1.A.b: if genre doesn't exist
                int genreSize = userProfile.getGenres().size();
                if(genreSize==3)
                {
                    // step 1.A.a.i.1: genre is full

                    // returns the oldest genre
                    String oldestGenre = userProfile.genreOrder.peek();

                    // removes the oldest genre
                    userProfile.musicProfileMap.remove(oldestGenre);

                    //remove it from the genre queue
                    userProfile.genreOrder.poll();

                    //add the new genre and musics
                    MusicProfile musicProfile = new MusicProfile(musicId, artistId);
                    HashMap<MusicProfile, Integer> musicProfileMap = new HashMap<MusicProfile, Integer>(10);
                    musicProfileMap.put(musicProfile, numberOfTimesPlayed);
                    userProfile.musicProfileMap.put(genre, musicProfileMap);

                    // add the genre order to the genreOrderQueue
                    userProfile.genreOrder.add(genre);
                }
                else
                {
                    //add the new genre and musics

                    MusicProfile musicProfile = new MusicProfile(musicId, artistId);
                    HashMap<MusicProfile, Integer> musicProfileMap = new HashMap<MusicProfile, Integer>(10);
                    musicProfileMap.put(musicProfile, numberOfTimesPlayed);
                    userProfile.musicProfileMap.put(genre, musicProfileMap);

                    // add the genre order to the genreOrderQueue
                    userProfile.genreOrder.add(genre);
                }
            break;
            }
        }
        if(userIdExistFlag == false) {
            System.out.println(userId + " doesnt exist in the cache. so it's added in the cache");
            // step 1.B : User doesn't exist
            // step 1.B.a : check the capacity
            int userProfileQueueSize = this.count;
            if (userProfileQueueSize == this.userIdCapacity) {
                // step 1.B.a : it's full so remove the oldest entry
                this.userProfiles.remove();
                this.count = this.count - 1;
            }
            // step 1.B.b : add the user profile
            this.count = this.count + 1;
            UserProfile up = new UserProfile(userId);
            MusicProfile musicProfile = new MusicProfile(musicId, artistId);
            HashMap<MusicProfile, Integer> musicProfileMap = new HashMap<MusicProfile, Integer>(10);
            musicProfileMap.put(musicProfile, numberOfTimesPlayed);
            up.musicProfileMap.put(genre, musicProfileMap);

            // add the genre order to the genreOrderQueue
            up.genreOrder.add(genre);
            this.userProfiles.add(up);
        }
        }

    public void addTestData()
    {

        this.addMusicToMusicProfile("music1", "artist1", 10);
        this.addMusicToMusicProfile("music2", "artist2", 20);
        this.addMusicToMusicProfile("music3", "artist3", 20);

        this.addMusicToMusicProfile("music4", "artist4", 20);
        this.addUserProfile("user1", "rock", "music1", "artist1", 10);
        this.addUserProfile("user1", "classic", "music2", "artist2", 10);

        for(UserProfile u:this.userProfiles){
            String userId = u.userId;
            if(userId.equals("user1") || userId.equals("user2"))
            {
                System.out.println(" genres : "+ u.getGenres());
                HashMap<MusicProfile, Integer> musicProfile = u.musicProfileMap.get(u.getGenres().iterator().next());
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
        Cache cache =  new Cache(100);
        cache.addTestData();
      int timesPlayed = cache.getTimesPlayedFromCache("music1");
        System.out.println("timesPlayed: "+timesPlayed);

        int timesPlayedByUser = cache.getTimesPlayedByUserFromCache("music1", "user1");
        System.out.println("timesPlayedByUser: "+timesPlayedByUser);


        ArrayList<String> topArtists = cache.getTopArtistsByUserGenreInCache("user1", "rock");
        System.out.println("Top Artists:" + topArtists);
    }

    //Method returns getTimesPlayed from the cache
    public int getTimesPlayedFromCache(String musicId){
        int res = 0;
        LinkedHashMap<MusicProfile, Integer> cacheMap = this.musicProfiles;
        for (MusicProfile mp: cacheMap.keySet())
        {
           // System.out.println("mp.musicId: " + mp.musicId);
            if(mp.musicId.equals(musicId)){
                // System.out.println("mp: " + mp);
                if(cacheMap.containsKey(mp)){
                    res = cacheMap.get(mp);
                    // System.out.println("res: " + res);
                    return res;
                }
            }
        }
        //Implement the "normal RMI query"
        return res;
    }

    //Method returns getTimesPlayedByUser from Cache
    //User specific getTimesPlayed is stored in the user profile
    public int getTimesPlayedByUserFromCache(String musicId, String userId) {
        int res = 0;
        Queue<UserProfile> userProfiles = this.userProfiles;
        for (UserProfile u : userProfiles) {
            if (u.userId.equals(userId)) {
                for(String genre : u.musicProfileMap.keySet()){
                    //System.out.println("Hello");
                    HashMap<MusicProfile, Integer> favMusic = u.musicProfileMap.get(genre);
                    for(MusicProfile mp : favMusic.keySet()){
                        if(mp.musicId.equals(musicId)){
                            res = favMusic.get(mp);
                          //  System.out.println("res: " + res);
                            return res;
                        }
                    }
                }
            }
        }
        return res;
        //Implement the rest rmi stuff
    }

    //Returns the top artists of a specific genre a user listens to
    //The artist ids are stored in the Music Profile, which is the value to the genre key
    public ArrayList<String> getTopArtistsByUserGenreInCache(String userId, String genre){
        ArrayList<String> res = new ArrayList<String>();
        Queue<UserProfile> userProfiles = this.userProfiles;
        for (UserProfile u : userProfiles){
           if(u.userId.equals(userId)){
               if(u.musicProfileMap.containsKey(genre)){
                   HashMap<MusicProfile, Integer> favMusic = u.musicProfileMap.get(genre);
                  // System.out.println("favMusic: "+favMusic);
                   for(MusicProfile mp : favMusic.keySet()){
                       res.add(mp.artistId);
                      // System.out.println(mp.artistId);
                    //   System.out.println("res: " + res);
                       return res;
                   }

               }
            }
        }
        //Implement the normal RMI stuff
        return res;
    }

}