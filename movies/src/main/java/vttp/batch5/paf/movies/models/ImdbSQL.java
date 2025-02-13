package vttp.batch5.paf.movies.models;

import java.util.Date;

public class ImdbSQL {
    private String imdbId;
    private float voteAverage;
    private Long voteCount;
    private Date releaseDate;
    private Double revenue;
    private Double budget;
    private Integer runtime;


    public ImdbSQL() {}

    public ImdbSQL(String imdbId, float voteAverage, Long voteCount, Date releaseDate, Double revenue, Double budget,
            Integer runtime) {
        this.imdbId = imdbId;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.budget = budget;
        this.runtime = runtime;
    }
    public String getImdbId() {
        return imdbId;
    }
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
    public float getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }
    public Long getVoteCount() {
        return voteCount;
    }
    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    public Double getRevenue() {
        return revenue;
    }
    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
    public Double getBudget() {
        return budget;
    }
    public void setBudget(Double budget) {
        this.budget = budget;
    }
    public Integer getRuntime() {
        return runtime;
    }
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    
}
