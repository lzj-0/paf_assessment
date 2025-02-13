package vttp.batch5.paf.movies.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp.batch5.paf.movies.models.ImdbMongo;
import vttp.batch5.paf.movies.models.ImdbSQL;
import vttp.batch5.paf.movies.services.MovieService;

@Component
public class Dataloader {

  @Autowired
  MovieService movieService;

  //TODO: Task 2
    public void loadData(String fileName) throws IOException, ParseException {

      ZipFile file = new ZipFile(getClass().getResource(fileName).toString());
      ZipEntry entry = file.getEntry("movies_post_2010.json");

      InputStream is = file.getInputStream(entry);
      InputStreamReader isr = new InputStreamReader(is);
      LineNumberReader lnr = new LineNumberReader(isr);

      String line = null;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      List<ImdbSQL> imdbSQLList = new ArrayList<>();
      List<ImdbMongo> imdbMongoList = new ArrayList<>();

      while ((line = lnr.readLine()) != null) {
        JsonReader jReader = Json.createReader(new StringReader(line));
        JsonObject jObj = jReader.readObject();

        if (sdf.parse(jObj.getString("release_date")).after(sdf.parse("2018-01-01")) 
                && (!movieService.idExists(jObj.getString("imdb_id")))) {

          String imdbId = jObj.getString("imdb_id");
          float voteAverage = (jObj.get("vote_average") == JsonValue.NULL) ? 0f : jObj.getJsonNumber("vote_average").bigDecimalValue().floatValue();
          Long voteCount = (jObj.get("vote_count") == JsonValue.NULL) ? 0l : jObj.getJsonNumber("vote_count").longValue();
          Date releaseDate = sdf.parse(jObj.getString("release_date"));
          Double revenue = (jObj.get("reveneue") == JsonValue.NULL) ? 0 : jObj.getJsonNumber("revenue").doubleValue();
          Double budget = (jObj.get("budget") == JsonValue.NULL) ? 0 : jObj.getJsonNumber("budget").doubleValue();
          Integer runtime = (jObj.get("runtime") == JsonValue.NULL) ? 0 : jObj.getInt("runtime");

          imdbSQLList.add(new ImdbSQL(imdbId, voteAverage, voteCount, releaseDate, revenue, budget, runtime));

          String title = (jObj.get("title") == JsonValue.NULL) ? "" : jObj.getString("title");
          List<String> directors = (jObj.get("director") == JsonValue.NULL) ? new ArrayList<String>() : Arrays.stream(jObj.getString("director").split(", ")).toList();
          String overview = (jObj.get("overview") == JsonValue.NULL) ? "" : jObj.getString("overview");
          String tagline = (jObj.get("tagline") == JsonValue.NULL) ? "" : jObj.getString("tagline");
          String genres = (jObj.get("genres") == JsonValue.NULL) ? "" : jObj.getString("genres");
          Float imdbRating = (jObj.get("imdb_rating") == JsonValue.NULL) ? 0f : jObj.getJsonNumber("imdb_rating").bigDecimalValue().floatValue();
          Long imdbVotes = (jObj.get("imdb_votes") == JsonValue.NULL) ? 0l : jObj.getJsonNumber("imdb_votes").longValue();

          imdbMongoList.add(new ImdbMongo(imdbId, title, directors, overview, tagline, genres, imdbRating, imdbVotes));
         }

         if (imdbSQLList.size() == 25 && imdbMongoList.size() == 25) {
            movieService.batchInsertIntoDB(imdbSQLList, imdbMongoList);
            imdbSQLList = new ArrayList<>();
            imdbMongoList = new ArrayList<>();
         }
    }

    if (imdbSQLList.size() > 0 || imdbMongoList.size() > 0) {
      movieService.batchInsertIntoDB(imdbSQLList, imdbMongoList);
    }

    System.out.println("completed");

    file.close();

    
  }

}
