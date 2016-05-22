package udacity.android.gkcs.popularmovies.model;

public class ReviewResult {
    private final Review[] results;
    private final String id;

    public ReviewResult(final Review[] results, String id) {
        this.results = results;
        this.id = id;
    }

    public Review[] getResults() {
        return results;
    }

    public String getId() {
        return id;
    }
}
