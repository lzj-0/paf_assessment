package vttp.batch5.paf.movies.models;

import java.util.List;

public class ImdbMongo {
    private String imdbId;
    private String title;
    private List<String> directors;
    private String overview;
    private String tagline;
    private String genres;
    private Float imdbRating;
    private Long imdbVotes;

    public ImdbMongo() {}

    public ImdbMongo(String imdbId, String title, List<String> directors, String overview, String tagline, String genres,
            Float imdbRating, Long imdbVotes) {
        this.imdbId = imdbId;
        this.title = title;
        this.directors = directors;
        this.overview = overview;
        this.tagline = tagline;
        this.genres = genres;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public Float getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Long getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(Long imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    

    
}
