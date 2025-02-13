package vttp.batch5.paf.movies.repositories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.batch5.paf.movies.models.ImdbSQL;

@Repository
public class MySQLMovieRepository {

  @Autowired
  private JdbcTemplate template;

  public static String INSERT_MOVIE_SQL = "insert into imdb values (?, ?, ?, ?, ?, ?, ?)";
  public static String IMDB_EXISTS_SQL = "select * from imdb where imdb_id = ?";
  public static String TABLE_EMPTY_SQL = "select * from imdb";


  // TODO: Task 2.3
  // You can add any number of parameters and return any type from the method
  public void batchInsertMovies(List<ImdbSQL> imdbs) {
    List<Object[]> params = imdbs.stream()
                  .map(imdb -> new Object[] {imdb.getImdbId(), imdb.getVoteAverage(), imdb.getVoteCount(),
                                            imdb.getReleaseDate(), imdb.getRevenue(), imdb.getBudget(), imdb.getRuntime()})
                  .collect(Collectors.toList());

    int[] added = template.batchUpdate(INSERT_MOVIE_SQL, params);
    
  }

  public boolean idExists(String imdbId) {
    SqlRowSet rs = template.queryForRowSet(IMDB_EXISTS_SQL, imdbId.toString());

    return rs.next();
  }

  public boolean isEmpty() {
    SqlRowSet rs = template.queryForRowSet(TABLE_EMPTY_SQL);
    return !rs.next();
  }


  // TODO: Task 3

  public SqlRowSet getMovieDetails(List<String> imdbIds) {

    String inSql = String.join(",", Collections.nCopies(imdbIds.size(), "?"));

    SqlRowSet rs = template.queryForRowSet(String.format("select sum(revenue) Revenue, sum(budget) Budget from imdb where imdb_id in (%s) group by null", inSql),
                    imdbIds.toArray());

    return rs;
  }


}
