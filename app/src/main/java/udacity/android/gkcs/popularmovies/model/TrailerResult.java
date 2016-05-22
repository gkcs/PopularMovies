package udacity.android.gkcs.popularmovies.model;

public class TrailerResult {
    private final Trailer[] results;
    private final String id;

    public TrailerResult(final Trailer[] results, String id) {
        this.results = results;
        this.id = id;
    }

    public Trailer[] getResults() {
        return results;
    }

    public String getId() {
        return id;
    }
}
