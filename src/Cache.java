import java.util.*;
public class Cache implements CacheInterface{

    private int musicIdCapacity = 100;
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
    private int getMusicProfileCacheSize()
    {
        return this.musicProfiles.size();
    }

    //adds music profiles to cache
    private void addMusicProfileToCache(String musicId, String artistId, int numberOfTimesPlayed)
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
        System.out.println("new cache entry : \t music Id : "+musicId+"\t times played : "+numberOfTimesPlayed);
    }
    // adds number of times played to user id
//    private void addTimesPlayedToUser(String userId, int numberoOfTimesPlayed)
//    {
//        boolean userIdExistFlag = false;
//
//        // add userId and timesplayed to the user profile
//        for(UserProfile userProfile: this.userProfiles)
//         {
//        // step 1.A : user id exists
//        if (userProfile.userId.equals(userId)) {
//            userIdExistFlag = true;
//            userProfile.addTimesPlayed(numberoOfTimesPlayed);
//            break;
//             }
//          }
//        if(userIdExistFlag==false)
//        {
//            {
//                // step 1.B : User doesn't exist
//                // step 1.B.a : check the capacity
//                int userProfileQueueSize = this.count;
//                if (userProfileQueueSize == this.userIdCapacity) {
//                    // step 1.B.a : it's full so remove the oldest entry
//                    this.userProfiles.remove();
//                    this.count = this.count - 1;
//                }
//                // step 1.B.b : add the user profile
//                this.count = this.count + 1;
//                UserProfile up = new UserProfile(userId);
//                up.addTimesPlayed(numberoOfTimesPlayed);
//                this.userProfiles.add(up);
//            }
//        }
//        System.out.println("new cache entry : \t user Id : "+userId+"\t times played : "+numberoOfTimesPlayed);
//
//    }
    // adds user profile
    private void addUserProfileToCache(String userId, String genre, String musicId, String artistId, int numberOfTimesPlayed)
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
            System.out.println("new cache entry : \t user Id : "+userId+"\t genre : " +genre+"\t music id : "+musicId+
                    "\t artist id : "+artistId+ "\t times played : "+numberOfTimesPlayed);
        }
        }

    public void addToCache(TimesPlayedTask task)
    {
        String artistId = "";
        this.addMusicProfileToCache(task.getMusicID(), artistId, (int) task.getResult());

    }
    public void addToCache(TimesPlayedByUserTask task)
    {
        String userId = task.getUserID();
        int numberOfTimesPlayed = (int) task.getResult();
        String musicId = task.getMusicID();
        String genre = "userTimes";
        String artistId = "";
        this.addUserProfileToCache(userId,genre,musicId,artistId,numberOfTimesPlayed);
        //this.addTimesPlayedToUser(userId, numberOfTimesPlayed);
    }
    public void addToCache(TopArtistsByUserGenreTask task) {
        String[] top3 = (String[]) task.getResult();
        String genre = task.getGenre();
        String userId = task.getUserID();
        for (int i = 0; i < top3.length; i++) {
            String artistId = top3[i];
            String musicId = "";
            int timesPlayed = 0;
            this.addUserProfileToCache(userId, genre, musicId, artistId, timesPlayed);
        }
    }

    //Method returns getTimesPlayed from the cache
    public TimesPlayedTask fetchFromCache(TimesPlayedTask task){

        String musicId = task.getMusicID();

        int res = 0;

        LinkedHashMap<MusicProfile, Integer> cacheMap = this.musicProfiles;

        for (MusicProfile mp: cacheMap.keySet()) {
           // System.out.println("mp.musicId: " + mp.musicId);
            if(mp.musicId.equals(musicId)){
                // System.out.println("mp: " + mp);
                if(cacheMap.containsKey(mp)){
                    res += cacheMap.get(mp);
                    // System.out.println("res: " + res);

                }
            }
        }
        task.setResult(res);
        System.out.println("cache entry accessed: \t music Id : "+musicId+"\t times played : "+res);
        return task;
    }


    //Method returns getTimesPlayedByUser from Cache
    //User specific getTimesPlayed is stored in the user profile
    public TimesPlayedByUserTask fetchFromCache(TimesPlayedByUserTask task) {
        String musicId = task.getMusicID();
        String userId = task.getUserID();
        String genre = "userTimes";
        int res = 0;

        Queue<UserProfile> userProfiles = this.userProfiles;
        for (UserProfile u : userProfiles) {
            if (u.userId.equals(userId)) {
                if(u.musicProfileMap.containsKey(genre)){
                    HashMap<MusicProfile, Integer> favMusic = u.musicProfileMap.get(genre);
                    // System.out.println("favMusic: "+favMusic);
                    for(MusicProfile mp : favMusic.keySet()){
                        if(mp.musicId.equals(musicId))
                        {
                            res = favMusic.get(mp);
                            break;
                        }
                        // System.out.println(mp.artistId);
                        //   System.out.println("res: " + res);
                        //
                    }


                }
            }
        }

        task.setResult(res);
        System.out.println("cache entry accessed: \t user Id : "+userId+"\t music id : "+musicId+"\t times played : "+res);
        return task;

    }

    //Returns the top artists of a specific genre a user listens to
    //The artist ids are stored in the Music Profile, which is the value to the genre key
    public TopArtistsByUserGenreTask fetchFromCache(TopArtistsByUserGenreTask task) {
        String userId = task.getUserID();
        String genre = task.getGenre();

        ArrayList<String> res = new ArrayList<>();
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
                    //
                   }


               }
            }
        }

        System.out.print("cache entry accessed: \t user Id : "+userId+"\t genre : "+genre+"\t top artists : ");
        for (int counter = 0; counter < res.size(); counter++) {
            System.out.print(res.get(counter)+"\t");
        }
        System.out.println();
        if(res.size()==3)
        task.setResult(res.toArray(new String[3]));
        return task;
    }
    public static void testCache()
    {
        Cache cache = new Cache(100);
        int zoneID = 1;
        String musicID = "1";
        String userID = "1";
        String genre = "rock";
        int timesPlayed = 10;
        String[] top3 = {"music1","music2","music3"};


        TimesPlayedTask task1 = new TimesPlayedTask(musicID, zoneID);
        task1.setResult(timesPlayed);
        cache.addToCache(task1);

        TimesPlayedTask task11 = new TimesPlayedTask(musicID, zoneID);
        cache.fetchFromCache(task11);
        System.out.println("count : "+task11.getResult());

        TimesPlayedByUserTask task2=  new TimesPlayedByUserTask(musicID, userID, zoneID);
        task2.setResult(20);
        cache.addToCache(task2);
        TimesPlayedByUserTask task21 =  new TimesPlayedByUserTask(musicID, userID, zoneID);
        cache.fetchFromCache(task21);
        System.out.println("count : "+task21.getResult());


        TopThreeMusicByUserTask task3 =  new TopThreeMusicByUserTask(userID, zoneID);

        TopArtistsByUserGenreTask task4 = new TopArtistsByUserGenreTask(userID, genre, zoneID);
        task4.setResult(top3);
        cache.addToCache(task4);
        TopArtistsByUserGenreTask task41 = new TopArtistsByUserGenreTask(userID, genre, zoneID);
        cache.fetchFromCache(task41);
        System.out.println("result : "+task41.getResult()[0]+"\t"+task41.getResult()[1]+"\t"+task41.getResult()[2]);

        TimesPlayedByUserTask task22 =  new TimesPlayedByUserTask(musicID, userID, zoneID);
        cache.fetchFromCache(task22);
        System.out.println("count : "+task22.getResult());

    }
    public static void main(String[] args)
    {
            testCache();
    }

}
