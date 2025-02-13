package vttp.batch5.paf.movies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vttp.batch5.paf.movies.bootstrap.Dataloader;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

@SpringBootApplication
public class MoviesApplication implements CommandLineRunner {

	@Autowired
	Dataloader dataloader;

	@Autowired
	MySQLMovieRepository mySQLMovieRepo;

	public static void main(String[] args) {
		SpringApplication.run(MoviesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String file;

		if (args.length > 0) {
			file = args[0];
		} else {
			file = "../data/movies_post_2010.zip";
		}

		if (mySQLMovieRepo.isEmpty()) {
			System.out.println("Adding data into DB...");
			dataloader.loadData(file);
		} else {
			System.out.println("Data has been loaded");
		}

	}

}
