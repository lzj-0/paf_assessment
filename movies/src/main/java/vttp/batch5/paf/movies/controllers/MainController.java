package vttp.batch5.paf.movies.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonArray;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import vttp.batch5.paf.movies.services.MovieService;

@RestController
@RequestMapping("/api")
public class MainController {

  @Autowired
  MovieService movieService;

  // TODO: Task 3
   @GetMapping("/summary")
   public ResponseEntity<String> getDirectors(@RequestParam Integer count) {
      JsonArray directorsArr = movieService.getProlificDirectors(count);

      return ResponseEntity.ok(directorsArr.toString());
   }

  
  // TODO: Task 4
  @GetMapping(path = "/summary/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> getReport(@RequestParam Integer count) throws JRException, IOException {

      File f = movieService.generatePDFReport(count);

      FileInputStream fis = new FileInputStream(f);

      return ResponseEntity.ok(fis.readAllBytes());
  }


}
