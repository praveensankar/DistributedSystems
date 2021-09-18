import java.rmi.RemoteException;


public class TimesPlayedTask extends Task<Integer> {

  private static final long serialVersionUID = 11L;

  private String musicID;

  TimesPlayedTask(String musicID, int zoneID) {
    super(zoneID);
    this.musicID = musicID;
  }

  public String getMusicID() {
    return musicID;
  }

  @Override
  public Task<Integer> execute(ServerInterface server) throws RemoteException {
    return server.getTimesPlayed(this);
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times. " + super.toString();
  }

  // public static void main(String[] args) {
  //
  //   try {
  //     Task<Integer> t1 = new TimesPlayedTask("M1234", 1);
  //     Task<Integer> t2 = new TimesPlayedByUserTask("M1234", "U111", 1);
  //     Task<String[]> t3 = new TopThreeMusicByUserTask("U111", 1);
  //     Task<String[]> t4 = new TopArtistsByMusicGenreTask("M1234", "rock", 1);
  //
  //     t1.setResult(6);
  //     t4.setResult(new String[]{"A111", "A222", "A333"});
  //
  //     System.out.println(t1.toString());
  //     System.out.println(t2.toString());
  //     System.out.println(t3.toString());
  //     System.out.println(t4.toString());
  //   } catch(Exception e) {
  //     e.printStackTrace();
  //   }
  // }
}
