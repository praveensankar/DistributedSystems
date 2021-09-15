class TopArtistsByMusicGenreTask extends Task<String[]> {

  String userID;
  String genre;

  TopArtistsByMusicGenreTask(String userID, String genre) {
    this.userID = userID;
    this.genre = genre;
    result = new String[3];
  }

  @Override
  public String toString() {
    return "Top three artists for genre " + genre
            + " and user " + userID
            + " were " + result[0] + ", " + result[1] + ", " + result[2]
            + ". " + super.toString();
  }

}
