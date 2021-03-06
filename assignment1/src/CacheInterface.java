public interface CacheInterface {
    public TimesPlayedTask fetchFromCache(TimesPlayedTask task);
    public TimesPlayedByUserTask fetchFromCache(TimesPlayedByUserTask task);
    public TopArtistsByUserGenreTask fetchFromCache(TopArtistsByUserGenreTask task);
    public TopThreeMusicByUserTask fetchFromCache(TopThreeMusicByUserTask task);
    public void addToCache(TimesPlayedTask task);
    public void addToCache(TimesPlayedByUserTask task);
    public void addToCache(TopArtistsByUserGenreTask task);
    public void addToCache(TopThreeMusicByUserTask task);
}
