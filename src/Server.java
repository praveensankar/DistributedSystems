import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

    private BufferedReader getReader() throws IOException {
      return new BufferedReader(new FileReader("../data/dummydataset.csv"));
    }

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
      int count = 0;

      try {

        BufferedReader br = getReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);

          // maybe could be replaced with an abstract class and functions to decrease
          // code duplication
          if (musicID.equals(record[0])) {
            count += Integer.parseInt(record[record.length - 1]);
          }
        }

      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.out.println("Logical error: Something went wrong with parsing!\n");
        e.printStackTrace();
      }

      return count;
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

    // easy, does not account for several artists right now.
    // Trying with buffered reader. Faster than scanner according to google
    // https://www.javatpoint.com/how-to-read-csv-file-in-java
    void readCSVfile() {

      String line = "";
      String splitBy = ",";

      try {
        BufferedReader br = new BufferedReader(new FileReader("../data/dummydataset.csv"));

        while ((line = br.readLine()) != null) {
          String[] record = line.split(splitBy);
          // 0 1 2 3 4
          // M A G U T
          // 0 1 2 3 4 5
          // M A A G U T
          String[] columnNames = {"MusicID", "ArtistID", "Genre", "UserID", "Times played"};

          // getting the number of artists by using mod "number of columns"
          int numOfArtists = (record.length % 5) + 1;

          System.out.print(columnNames[0] + ": " + record[0] + "\t");

          System.out.print(columnNames[1] + ": ");
          for (int i = 1; i <= numOfArtists; i++)
            System.out.print(record[i] + " ");

          System.out.print("\t");

          for (int i = 2; i < columnNames.length; i++)
            System.out.print(columnNames[i] + ": " + record[i + numOfArtists - 1] + "\t");

          System.out.println("");
        }


      } catch(IOException e) {
          e.printStackTrace();
      }

    }

    public static void main(String[] args) {

      Server s = new Server();

      s.readCSVfile();

      System.out.println("\nTimes played:");
      System.out.println("M9: " + s.getTimesPlayed("M9"));
      System.out.println("M1: " + s.getTimesPlayed("M1"));
      System.out.println("M14: " + s.getTimesPlayed("M14"));
    }
}
