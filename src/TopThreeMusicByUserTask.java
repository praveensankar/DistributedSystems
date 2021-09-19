public class TopThreeMusicByUserTask extends Task<String[]> {

  private static final long serialVersionUID = 13L;

  String userID;

  TopThreeMusicByUserTask(String userID, int zoneID) {
    super(zoneID);
    this.userID = userID;
    result = new String[3];
  }

  public String getUserID() {
    return userID;
  }

  public boolean hasResult() {
    for (String s : result)
      if (s != null)
        return true;

    return false;
  }

  @Override
  public String toString() {
    return "Top three musics for user " + userID + " were "
            + result[0] + ", " + result[1] + ", " + result[2]
            + ". " + super.toString();
  }
}
