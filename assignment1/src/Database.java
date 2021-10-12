import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Database class.
 *
 * This class is responsible for querying the database.
 * It contains all the algorithms for the different queries.
 *
 */
class Database {

  String datafile = "../data/dataset.csv";

  int musicIndex = 0;
  int artistIndex = 1;
  int genreIndex = 2;
  int userIndex = 3;
  int countIndex = 4;


  // public Database(String datafile) {
  //   this.datafile = datafile;
  // }

  private BufferedReader newReader() throws IOException {
    return new BufferedReader(new FileReader(datafile));
  }


  /**
   * Executes the query 'getTimesPlayedByUser'. It takes in the task
   * corresponding to it, the 'TimesPlayedByUserTask', queries the dataset,
   * and places the result in the task object by using task.setResult().
   *
   * @param  task               The TimesPlayedByUserTask to be executed
   * @return                    Returns the same task, updated with the result
   */
  public TimesPlayedTask executeQuery(TimesPlayedTask task) {

    int count = 0;

    try {

      BufferedReader br = newReader();
      String line, delimiter = ",";

      while ((line = br.readLine()) != null) {
        String[] record = line.split(delimiter);

        if (record[musicIndex].equals(task.getMusicID()))
          count += Integer.parseInt(record[countIndex]);
      }

      br.close();

    } catch(Exception e) {
      e.printStackTrace();
    }

    task.setResult(count);

    return task;
  }


  /**
   * Executes the query 'getTimesPlayed'. It takes in the task
   * corresponding to it, the 'TimesPlayedTask', queries the dataset,
   * and places the result in the task object by using task.setResult().
   *
   * @param  task               The TimesPlayedTask to be executed
   * @return                    Returns the same task, updated with the result
   */
  public TimesPlayedByUserTask executeQuery(TimesPlayedByUserTask task) {

    int count = 0;

    try {

      BufferedReader br = newReader();
      String line, delimiter = ",";

      while ((line = br.readLine()) != null) {

        String[] record = line.split(delimiter);
        String genre = record[genreIndex];
        String artistId = record[artistIndex];

        if (record[musicIndex].equals(task.getMusicID()) &&
            record[userIndex].equals(task.getUserID())) {

          count += Integer.parseInt(record[countIndex]);

        }
      }

      br.close();

    } catch(Exception e) {
      e.printStackTrace();
    }

    task.setResult(count);

    return task;

  }


  /**
   * Executes the query 'getTopThreeMusicByUser'. It takes in the task
   * corresponding to it, the 'TopThreeMusicByUserTask', queries the dataset,
   * and places the result in the task object by using task.setResult().
   *
   * @param  task               The TopThreeMusicByUserTask to be executed
   * @return                    Returns the same task, updated with the result
   */
  public TopThreeMusicByUserTask executeQuery(TopThreeMusicByUserTask task) {

    String[] top3 = new String[3];

    try {
      BufferedReader br = newReader();
      String line, delimiter = ",";

      HashMap<String, Integer> musicToCount = new HashMap<>();

      while ((line = br.readLine()) != null) {
        String[] record = line.split(delimiter);
        String user = record[userIndex];

        if (user.equals(task.getUserID())) {

          String musicID = record[musicIndex];
          int timesPlayed = Integer.parseInt(record[countIndex]);
          int artistCount = musicToCount.getOrDefault(musicID, 0);

          musicToCount.put(musicID, artistCount + timesPlayed);

        }
      }

      br.close();

      top3 = getTop3(musicToCount);

    } catch(Exception e) {
      e.printStackTrace();
    }

    task.setResult(top3);

    return task;

  }


  /**
   * Executes the query 'getTopArtistsByUserGenre'. It takes in the task
   * corresponding to it, the 'TopArtistsByUserGenreTask', queries the dataset,
   * and places the result in the task object by using task.setResult().
   *
   * @param  task               The TopArtistsByUserGenreTask to be executed
   * @return                    Returns the same task, updated with the result
   */
  public TopArtistsByUserGenreTask executeQuery(TopArtistsByUserGenreTask task) {

    String[] top3 = new String[3];

    try {

      BufferedReader br = newReader();
      String line, delimiter = ",";

      // Accumulating all the relevant values and placing them in a HashMap
      HashMap<String, Integer> artistToCount = new HashMap<>();

      while ((line = br.readLine()) != null) {
        String[] record = line.split(delimiter);
        String user = record[userIndex];
        String genre = record[genreIndex];

        if (user.equals(task.getUserID()) && genre.equals(task.getGenre())) {

          // Getting the count of listens
          int timesPlayed = Integer.parseInt(record[countIndex]);
          String artistID = record[artistIndex];
          int artistCount = artistToCount.getOrDefault(artistID, 0);

          artistToCount.put(artistID, timesPlayed + artistCount);
        }
      }

      br.close();

      // Performing a simple insertion sort for each entry

      top3 = getTop3(artistToCount);

    } catch (Exception e) {
      e.printStackTrace();
    }

    task.setResult(top3);

    return task;

  }


  /**
   * Returns a music profile for music with id 'musicID'. If there is no
   * such music, then it returns 'null'.
   *
   * @param  musicID               The music ID
   * @return                       MusicProfile, or 'null' if not found.
   */
  public MusicProfile getMusicProfile(String musicID) {

    try {

      BufferedReader br = newReader();
      String line, delimiter = ",";

      while ((line = br.readLine()) != null) {
        String[] record = line.split(delimiter);

        if (record[musicIndex].equals(musicID)) {
          return new MusicProfile(musicID, record[1]);
        }
      }

      br.close();

    } catch(Exception e) {
      e.printStackTrace();
    }

    return null;
  }


  /**
   * A helper function that returns the keys that map to
   * the highest three values. The keyes are sorted on their values.
   * Highest key is first, lowest key is last.
   *
   * @param  map              The HashMap<String, Integer> to be sorted.
   * @return                  String array with the keys pointing to highest values.
   */
  private String[] getTop3(HashMap<String, Integer> map) {

    String[] top3 = new String[3];
    int[] count = new int[3];

    for (HashMap.Entry<String, Integer> entry : map.entrySet()) {

      int value = entry.getValue();

      int lastIndex = count.length - 1;

      if (value > count[lastIndex]) {

        String key = entry.getKey();

        int i = lastIndex - 1;

        while (i >= 0 && value > count[i]) {
          count[i + 1] = count[i];
          top3[i + 1] = top3[i--];
        }

        count[i + 1] = value;
        top3[i + 1] = key;
      }
    }

    return top3;
  }


  public static void main(String[] args) {

    Database db = new Database();

    db.datafile = "../data/dummydataset.csv";

    System.out.println("\nDummy dataset");

    // Dummy dataset
    System.out.println("\nTimes played");

    TimesPlayedTask[] tp = {
      new TimesPlayedTask("M1", 0),
      new TimesPlayedTask("M1", 0),
      new TimesPlayedTask("M1", 0),
      new TimesPlayedTask("M11", 0)
    };

    System.out.println("\nTimes played by user");

    TimesPlayedByUserTask[] tpu = {
      new TimesPlayedByUserTask("M1", "U1", 0),
      new TimesPlayedByUserTask("M1", "U2", 0),
      new TimesPlayedByUserTask("M1", "U3", 0),
      new TimesPlayedByUserTask("M10", "U4", 0)
    };

    System.out.println("\nTop 3 music");

    TopArtistsByUserGenreTask[] tpmg = {
      new TopArtistsByUserGenreTask("U1", "Rock", 0),
      new TopArtistsByUserGenreTask("U3", "Rock", 0),
      new TopArtistsByUserGenreTask("U2", "Rock", 0),
      new TopArtistsByUserGenreTask("U11", "Rock", 0)
    };

    System.out.println("\nTimes played");

    TopThreeMusicByUserTask[] tpmu = {
      new TopThreeMusicByUserTask("U1", 0),
      new TopThreeMusicByUserTask("U2", 0),
      new TopThreeMusicByUserTask("U3", 0),
      new TopThreeMusicByUserTask("U11", 0)
    };


    for (TimesPlayedTask t : tp) {
      db.executeQuery(t);
      System.out.println(t.toString());
    }
    for (TimesPlayedByUserTask t : tpu) {
      db.executeQuery(t);
      System.out.println(t.toString());
    }
    for (TopThreeMusicByUserTask t : tpmu) {
      db.executeQuery(t);
      System.out.println(t.toString());
    }
    for (TopArtistsByUserGenreTask t : tpmg) {
      db.executeQuery(t);
      System.out.println(t.toString());
    }

    System.out.println("\nMusicProfile");
    MusicProfile mp = db.getMusicProfile("M11");
    System.out.println(mp.toString());

  }

}
