public interface CacheInterface {
    public TimesPlayedTask fetchFromCache(TimesPlayedTask task);
    public TimesPlayedByUserTask fetchFromCache(TimesPlayedByUserTask task);
    public TopArtistsByMusicGenreTask fetchFromCache(TopArtistsByMusicGenreTask task);
    public void addToCache(TimesPlayedTask task);
    public void addToCache(TimesPlayedByUserTask task);
    public void addToCache(TopArtistsByMusicGenreTask task);
}
