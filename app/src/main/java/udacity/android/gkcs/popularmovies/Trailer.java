package udacity.android.gkcs.popularmovies;

public class Trailer {
    private final String id;
    private final String iso_639_1;
    private final String key;
    private final String name;
    private final String site;
    private final String size;
    private final String type;

    public Trailer(final String id,
                   final String iso_639_1,
                   final String key,
                   final String name,
                   final String site,
                   final String size,
                   final String type) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}
