package udacity.android.gkcs.popularmovies.model;

public class Review {
    private final String id;
    private final String content;

    public Review(final String id, final String content, final String author, final String url) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.url = url;
    }

    private final String author;
    private final String url;

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }
}

