class TopThreeMusicByUserTask extends Task<String[]> {

  String userID;

  TopThreeMusicByUserTask(String userID) {
    this.userID = userID;
    result = new String[3];
  }

  @Override
  public String toString() {
    return "Top three musics for user " + userID + " were "
            + result[0] + ", " + result[1] + ", " + result[2]
            + ". " + super.toString();
  }
}
