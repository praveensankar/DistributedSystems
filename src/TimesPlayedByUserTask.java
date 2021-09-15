class TimesPlayedByUserTask extends Task<Integer> {

  String musicID, userID;

  TimesPlayedByUserTask(String musicID, String userID) {
    this.musicID = musicID;
    this.userID = userID;
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times by user " + userID
            + ". " + super.toString();

  }
}
