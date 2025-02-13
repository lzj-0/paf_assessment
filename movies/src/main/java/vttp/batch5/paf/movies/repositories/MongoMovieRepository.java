package vttp.batch5.paf.movies.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vttp.batch5.paf.movies.models.ImdbMongo;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@Repository
public class MongoMovieRepository {

   @Autowired
   MongoTemplate template;


 // TODO: Task 2.3
 // You can add any number of parameters and return any type from the method
 // You can throw any checked exceptions from the method
 // Write the native Mongo query you implement in the method in the comments
 //
 //    native MongoDB query here
   // db.imdb.insertMany([
   //    {_id : ..., title : ..., directors: [...], ....},
   //    {_id : ..., title : ..., directors: [...], ...},
   //    {...},
   //    ...
   // ])
 //
 public boolean batchInsertMovies(List<ImdbMongo> imdbs) {
   List<Document> docsToInsert = new ArrayList<>();

   for (ImdbMongo imdb : imdbs) {

      JsonArrayBuilder jab = Json.createArrayBuilder();
      imdb.getDirectors().forEach(dir -> jab.add(dir));

      Document directorDoc = Document.parse(Json.createObjectBuilder().add("_id", imdb.getImdbId())
                              .add("title", imdb.getTitle())
                              .add("directors", jab)
                              .add("overview", imdb.getOverview())
                              .add("tagline", imdb.getTagline())
                              .add("genres", imdb.getGenres())
                              .add("imdb_rating", imdb.getImdbRating())
                              .add("imdb_votes", imdb.getImdbVotes())
                              .build()
                              .toString());
      docsToInsert.add(directorDoc);
   }

   Collection<Document> newDocs = template.insert(docsToInsert, "imdb");

   List<Document> docs = new ArrayList<>(newDocs);

   if (docs.size() == imdbs.size()) {
      return true;
   } else {
      throw new RuntimeException("Fail to insert data into MongoDB");
   }

 }

 // TODO: Task 2.4
 // You can add any number of parameters and return any type from the method
 // You can throw any checked exceptions from the method
 // Write the native Mongo query you implement in the method in the comments
 //
 //    native MongoDB query here
//  db.imdb.insert({
//    imdb_ids: ....,
//    error: ....,
//    timestamp: ....
//  })
 //
 public void logError(Document error) {
   template.insert(error, "errors");
 }


 // TODO: Task 3
 // Write the native Mongo query you implement in the method in the comments
 //
 //    native MongoDB query here

//  db.imdb.aggregate([
//    {
//        $match : {"directors" : {$ne : ""}}
//    },
//    {
//        $unwind : "$directors"
//    },
//        {
//        $group: {
//            _id : "$directors",
//            count : {$sum : 1},
//            movies : {$push : "$_id"}
//        }
//    }
//    ,
//    {
//        $sort : {count : -1}
//    },
//    {
//        $limit : <count>
//    }
// ])
 public List<Document> getProlificDirectors(Integer count) {
   Criteria criteria = Criteria.where("directors").ne("");
   MatchOperation notEmpty = Aggregation.match(criteria);

   UnwindOperation unwindDirectors = Aggregation.unwind("directors");

   GroupOperation getMovieCount = Aggregation.group("directors").count().as("count")
                                                               .push("_id").as("movies");

   SortOperation sortByCount = Aggregation.sort(Sort.by(Direction.DESC, "count"));
   LimitOperation limit = Aggregation.limit(count);

   Aggregation pipeline = Aggregation.newAggregation(notEmpty, unwindDirectors, getMovieCount, sortByCount, limit);

   return template.aggregate(pipeline, "imdb", Document.class).getMappedResults();
}

}
