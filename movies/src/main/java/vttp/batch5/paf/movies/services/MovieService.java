package vttp.batch5.paf.movies.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;
import vttp.batch5.paf.movies.models.ImdbMongo;
import vttp.batch5.paf.movies.models.ImdbSQL;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

@Service
public class MovieService {

  @Autowired
  MySQLMovieRepository mySQLMovieRepo;
  
  @Autowired
  MongoMovieRepository mongoMovieRepo;

  @Value("${jasperreport.name}")
  private String name;

  @Value("${jasperreport.batch}")
  private String batch;

  // TODO: Task 2
  @Transactional
  public void batchInsertIntoDB(List<ImdbSQL> imdbSQL, List<ImdbMongo> imdbMongo) {

    try {
      if (imdbSQL.size() > 0) {
        mySQLMovieRepo.batchInsertMovies(imdbSQL);
      }
  
      if (imdbMongo.size() > 0) {
        mongoMovieRepo.batchInsertMovies(imdbMongo);
      }

    } catch(Exception e) {

      JsonArrayBuilder jab = Json.createArrayBuilder();
      
      imdbMongo.forEach(imdb -> jab.add(imdb.getImdbId()));

      JsonObject error = Json.createObjectBuilder()
          .add("imdb_ids", Json.createArrayBuilder().add(jab))
          .add("error", e.getMessage())
          .add("timestamp", LocalDateTime.now().toString())
          .build();

      mongoMovieRepo.logError(Document.parse(error.toString()));

      throw new RuntimeException("Error inserting data into MYSQL/MongoDB");
    }

  }

  public boolean idExists(String imdbId) {
    return mySQLMovieRepo.idExists(imdbId);
  }
  

  // TODO: Task 3
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public JsonArray getProlificDirectors(Integer count) {
      List<Document> directors = mongoMovieRepo.getProlificDirectors(count);

      JsonArrayBuilder jab = Json.createArrayBuilder();

      for (Document director : directors) {

        SqlRowSet rs = mySQLMovieRepo.getMovieDetails(director.getList("movies", String.class));

        if (rs.next()) {

          jab.add(Json.createObjectBuilder().add("director_name", director.getString("_id"))
                                            .add("movies_count", director.getInteger("count"))
                                            .add("total_revenue", rs.getDouble("Revenue"))
                                            .add("total_budget", rs.getDouble("Budget")));
          }
        }


      return jab.build();

  }


  // TODO: Task 4
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public File generatePDFReport(Integer count) throws FileNotFoundException, JRException {
      
      JsonObject jObj = Json.createObjectBuilder().add("name", name)
                              .add("batch", batch).build();

      JsonArray jArray = getProlificDirectors(count);
      JsonArrayBuilder jab = Json.createArrayBuilder();

      for (JsonValue jVal : jArray) {
        jab.add(Json.createObjectBuilder().add("director", jVal.asJsonObject().getString("director_name"))
                                          .add("count", jVal.asJsonObject().getInt("movies_count"))
                                          .add("revenue", jVal.asJsonObject().getJsonNumber("total_revenue").doubleValue())
                                          .add("budget", jVal.asJsonObject().getJsonNumber("total_budget").doubleValue()));
      }

      JsonArray newJsonArray = jab.build();

      JsonDataSource reportDS = new JsonDataSource(new ByteArrayInputStream(jObj.toString().getBytes()));

      JsonDataSource directorDS = new JsonDataSource(new ByteArrayInputStream(newJsonArray.toString().getBytes()));

      Map<String, Object> params = new HashMap<>();

      params.put("DIRECTOR_TABLE_DATASET", directorDS);

      InputStream directorReportStream = new FileInputStream(new File("../data/director_movies_report.jrxml"));

      JasperReport report = JasperCompileManager.compileReport(directorReportStream);

      JasperPrint print = JasperFillManager.fillReport(report, params, reportDS);

      JRPdfExporter exporter = new JRPdfExporter();
      exporter.setExporterInput(new SimpleExporterInput(print));
      exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("directorsReport.pdf"));

      SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
      reportConfig.setSizePageToContent(true);
      reportConfig.setForceLineBreakPolicy(false);

      SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
      exportConfig.setMetadataAuthor("Lee");
      exportConfig.setAllowedPermissionsHint("PRINTING");

      exporter.setConfiguration(reportConfig);
      exporter.setConfiguration(exportConfig);

      exporter.exportReport();

      return new File("directorsReport.pdf");

  }

}
