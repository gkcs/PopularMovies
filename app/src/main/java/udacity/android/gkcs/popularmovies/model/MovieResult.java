package udacity.android.gkcs.popularmovies.model;

public class MovieResult {
    private final Movie[] results;

    public MovieResult(final Movie[] results) {
        this.results = results;
    }

    public Movie[] getResults() {
        return results;
    }
}
