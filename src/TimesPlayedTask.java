class TimesPlayedTask extends Task<Integer> {

  private String musicID;

  TimesPlayedTask(String musicID) {
    this.musicID = musicID;
  }

  @Override
  public String toString() {
    return "Music " + musicID
            + " was played " + result
            + " times. " + super.toString();
  }

  public static void main(String[] args) {
    Task<Integer> t1 = new TimesPlayedTask("M1234");
    Task<Integer> t2 = new TimesPlayedByUserTask("M1234", "U111");
    Task<String[]> t3 = new TopThreeMusicByUserTask("U111");
    Task<String[]> t4 = new TopArtistsByMusicGenreTask("M1234", "rock");

    t1.setResult(6);
    t4.setResult(new String[]{"A111", "A222", "A333"});

    System.out.println(t1.toString());
    System.out.println(t2.toString());
    System.out.println(t3.toString());
    System.out.println(t4.toString());
  }
}
