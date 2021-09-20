
public class TimesPlayedTask extends Task<Integer> {

  private static final long serialVersionUID = 11L;

  private String musicID;

  TimesPlayedTask(String musicID, int zoneID) {
    super(zoneID);
    this.musicID = musicID;
    result = 0;
  }

  public String getMusicID() {
    return musicID;
  }

  public boolean hasResult() {
    return result != 0;
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times. " + super.toString();
  }

}
