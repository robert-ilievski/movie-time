package mk.ukim.finki.movietime.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.movietime.model.Director;
import mk.ukim.finki.movietime.model.Movie;
import mk.ukim.finki.movietime.service.MovieService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFParser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

  private static final String RESOURCE_URL = "http://dbpedia.org/resource/{movie}";

  @Override
  public Movie getMovieDetails(String movieName) {
    String movieResourceUrl = RESOURCE_URL.replace("{movie}", movieName);
    Movie movieModel = new Movie();

    Model model = ModelFactory.createDefaultModel();
    RDFParser.source(movieResourceUrl).httpAccept("text/turtle").parse(model.getGraph());

    Resource movieResource = model.getResource(movieResourceUrl);
    createMovieModel(movieName, movieResource, movieModel);
    return null;
  }

  private String label;
  private String name;
  private String shortDescription;
  private String description;
  private int runtime;
  private Director director;
  private String directorLabel;
  /**
   * Adds the label, name, description, runtime and director to the {@link Movie} model for the given movie.
   *
   * @param movieName movie name.
   * @param movieResource movie resource.
   * @param movieModel movie model class.
   */
  private static void createMovieModel(String movieName, Resource movieResource, Movie movieModel) {
  }
}
