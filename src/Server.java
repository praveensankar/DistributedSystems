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

    private BufferedReader newReader() throws IOException {
      return new BufferedReader(new FileReader("../data/dataset.csv"));
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

        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);

          // maybe could be replaced with an abstract class and functions to decrease
          // code duplication
          if (record[0].equals(musicID)) {
            count += Integer.parseInt(record[record.length - 1]);
          }
        }

      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with parsing!\n");
        e.printStackTrace();
      }

      return count;
    }

    // Question: is there several records for same musicID and userID??
    // If so, this needs to be modified. Have now modified it. But not sure if necessary.
    // If this is the case, then the top three will be more complicated
    public int getTimesPlayedByUser(String musicID, String userID) {
      int count = 0;
      try {
        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);

          if (record[0].equals(musicID) && record[record.length - 2].equals(userID)) {
            // return Integer.parseInt(record[record.length - 1]);
            count += Integer.parseInt(record[record.length - 1]);
          }
        }
      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with the parsing!");
        e.printStackTrace();
      }

      return count;
    }

    // Here I am assuming that there is only one record per song for a specific user.
    public String[] getTopThreeMusicByUser(String userID) {
      String[] top3 = new String[3];
      int[] count = new int[3];

      try {
        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);
          String user = record[record.length - 2];
          int timesPlayed = Integer.parseInt(record[record.length - 1]);

          if (user.equals(userID) && timesPlayed > count[2]) {

            int i = 1;
            while (i >= 0 && timesPlayed > count[i]) {
              count[i + 1] = count[i];
              top3[i + 1] = top3[i--];
            }

            count[i + 1] = timesPlayed;
            top3[i + 1] = record[0];

            // // can also write a simple sort function (e.g. insertion sort)
            // // if same or under second
            // if (timesPlayed <= count[1]) {
            //   count[2] = timesPlayed;
            //   top3[2] = record[0];
            // // if same or under first
            // } else if (timesPlayed <= count[0]) {
            //   // move the second place to third place
            //   count[2] = count[1];
            //   top3[2] = top3[1];
            //   // place it at second place
            //   count[1] = timesPlayed;
            //   top3[1] = record[0];
            // // if over first
            // } else {
            //   // move second to third
            //   count[2] = count[1];
            //   top3[2] = top3[1];
            //   // move first to second
            //   count[1] = count[0];
            //   top3[1] = top3[0];
            //   // place it at first place
            //   count[0] = timesPlayed;
            //   top3[0] = record[0];
            // }

          }

        }
      } catch(IOException e) {
        e.printStackTrace();
      } catch(NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with the parsing!\n");
        e.printStackTrace();
      }

      return top3;
    }

    public String[] getTopArtistsByMusicGenre(String userID, String genre) {
      String[] top3 = new String[3];
      int count[] = new int[3];

      try {
        BufferedReader br = newReader();
        String line, delimiter = ",";

        while ((line = br.readLine()) != null) {
          String[] record = line.split(delimiter);
          String user = record[record.length - 2];
          String g = record[record.length - 3];

          if (user.equals(userID) && g.equals(genre)) {
            // TODO
          }
        }

      } catch(IOException e) {
        e.printStackTrace();
      } catch (NumberFormatException e) {
        System.err.println("Logical error: Something went wrong with the parsing!\n");
        e.printStackTrace();
      }

      return top3;
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

      // s.readCSVfile();

      System.out.println("\nDummy dataset");

      // Dummy dataset
      System.out.println("\nTimes played");
      System.out.println("M9: " + s.getTimesPlayed("M9"));
      System.out.println("M1: " + s.getTimesPlayed("M1"));
      System.out.println("M14: " + s.getTimesPlayed("M14"));

      System.out.println("\nTimes played by user");
      System.out.println("M1 and U1: " + s.getTimesPlayedByUser("M1", "U1"));
      System.out.println("M1 and U2: " + s.getTimesPlayedByUser("M1", "U2"));
      System.out.println("M1 and U3: " + s.getTimesPlayedByUser("M1", "U3"));
      System.out.println("M10 and U4: " + s.getTimesPlayedByUser("M10", "U4"));

      System.out.println("\nTop 3 music");
      String user = "U2";
      String[] top3 = s.getTopThreeMusicByUser(user);
      System.out.print(user + ": ");

      for (String m : top3)
        System.out.print(m + " ");

      System.out.println("\n\nReal dataset");


      // Real dataset
      System.out.println("\nTimes played");
      System.out.println("MZnK007OYs: " + s.getTimesPlayed("MZnK007OYs"));
      System.out.println("MK3r9RF0Fz: " + s.getTimesPlayed("MK3r9RF0Fz"));
      System.out.println("Mnni7iWPl2: " + s.getTimesPlayed("Mnni7iWPl2"));

      System.out.println("\nTimes played by user");
      System.out.println("MZnK007OYs and UQ9lO8EhXd: " + s.getTimesPlayedByUser("MZnK007OYs", "UQ9lO8EhXd"));
      System.out.println("MZnK007OYs and Uo59ASIkor: " + s.getTimesPlayedByUser("MZnK007OYs", "Uo59ASIkor"));
      System.out.println("MK3r9RF0Fz and U1hn1abhsA: " + s.getTimesPlayedByUser("MK3r9RF0Fz", "U1hn1abhsA"));
      System.out.println("MeD0M4CGzg and Url3aJX8dX: " + s.getTimesPlayedByUser("MeD0M4CGzg", "Url3aJX8dX"));

      System.out.println("\nTop 3 music");
      user = "U5etSKm4EW";
      top3 = s.getTopThreeMusicByUser(user);
      System.out.println(user + ": ");

      for (String m : top3)
        System.out.println(m + ": " + s.getTimesPlayedByUser(m, user));

      System.out.println("");
    }
}
