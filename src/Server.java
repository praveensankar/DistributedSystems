import java.util.ArrayList;

public class Server implements ServerInterface {

    // choosing arraylist as the waitlist might be higher than ten.
    // private ArrayList<Client> waitList = new ArrayList<>();
    //
    // /**
    //  * Local methods
    //  */
    //
    // public int getWaitListSize() {
    //   return waitList.size();
    // }
    //
    //
    // public void addToWaitList(Client client) {
    //   waitList.add(client);
    // }
    //
    // private Client removeFromWaitList() {
    //   return waitList.remove(0);
    // }

    /**
     * Remote methods
     */
    public String sayHello() {
        return "Hello, world!";
    }

    public int add(int a, int b) {
        return a+b;
    }

    public int getTimesPlayed(String musicID) {
      // TODO
      return 0;
    }

    public int getTimesPlayedByUser(String musicID, String userID) {
      // TODO
      return 0;
    }

    public String[] getTopThreeMusicByUser(String userID) {
      // TODO
      return null;
    }

    public String getTopArtistByMusicGenre(String userID, String genre) {
      // TODO
      return null;
    }

    void readCSVfile() {

    }
}
