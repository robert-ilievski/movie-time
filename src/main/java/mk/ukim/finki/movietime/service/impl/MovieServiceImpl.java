package mk.ukim.finki.movietime.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.movietime.model.Director;
import mk.ukim.finki.movietime.model.Movie;
import mk.ukim.finki.movietime.service.MovieService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.sparql.core.ResultBinding;
import org.apache.jena.sparql.engine.binding.Binding;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static mk.ukim.finki.movietime.model.prefixes.Prefixes.DBO;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.DBR;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.RDF;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.RDFS;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.SCHEMA;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.SPARQL_ENDPOINT;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

  @Override
  public Movie getMovieDetails(String movieName) {
    String movieResourceUrl = DBR + movieName;
    Movie movieModel = new Movie();

    Model model = ModelFactory.createDefaultModel();
    RDFParser.source(movieResourceUrl).httpAccept("text/turtle").parse(model.getGraph());

    Resource movieResource = model.getResource(movieResourceUrl);
    createMovieModel(movieName, movieResource, movieModel);
    addDirectorInformationToMovieModel(movieResource, movieModel);
    addDirectedMovies(movieModel);

    return movieModel;
  }

  /**
   * Adds the label, name, description and runtime to the {@link Movie} model for the given movie.
   *
   * @param movieName     movie name.
   * @param movieResource movie resource.
   * @param movieModel    movie model class.
   */
  private static void createMovieModel(String movieName, Resource movieResource, Movie movieModel) {
    movieModel.setLabel(movieName);
    movieModel.setName(movieName.replace("_", " "));

    String shortDescription = movieResource.getProperty(new PropertyImpl(RDFS + "comment"), "en") == null ? null
        : movieResource.getProperty(new PropertyImpl(RDFS + "comment"), "en")
        .getObject().toString().replace("@en", "").replace("\"", "");
    String description = movieResource.getProperty(new PropertyImpl(DBO + "abstract"), "en") == null ? null
        : movieResource.getProperty(new PropertyImpl(DBO + "abstract"), "en")
        .getObject().toString().replace("@en", "").replace("\"", "");
    String runtime = movieResource.getProperty(new PropertyImpl(DBO + "Work/runtime")) == null ? null
        : movieResource.getProperty(new PropertyImpl(DBO + "Work/runtime")).getObject().toString();

    movieModel.setShortDescription(shortDescription);
    movieModel.setDescription(description);
    movieModel.setRuntime(runtime == null ? null : runtime.substring(0, runtime.indexOf("^^")));
  }

  /**
   * Adds director name, abstract and birthDate to the given movie model.
   *
   * @param movieResource movie resource.
   * @param movieModel    movie data model.
   */
  private static void addDirectorInformationToMovieModel(Resource movieResource, Movie movieModel) {
    Director directorModel = new Director();
    String directorUrl = movieResource.getPropertyResourceValue(new PropertyImpl(DBO + "director"))
        .toString();
    String directorLabel = directorUrl.substring(28);

    Model model = ModelFactory.createDefaultModel();
    RDFParser.source(directorUrl).httpAccept("text/turtle").parse(model.getGraph());
    Resource directorResource = model.getResource(directorUrl);

    String name = directorResource.getProperty(new PropertyImpl(RDFS + "label"), "en") == null ? null
        : directorResource.getProperty(new PropertyImpl(RDFS + "label"), "en")
        .getObject().toString().replace("@en", "");
    String description = directorResource.getProperty(new PropertyImpl(DBO + "abstract"), "en") == null ? null
        : directorResource.getProperty(new PropertyImpl(DBO + "abstract"), "en")
        .getObject().toString().replace("@en", "");
    String birthDate = directorResource.getProperty(new PropertyImpl(DBO + "birthDate"), "en") == null ? null
        : directorResource.getProperty(new PropertyImpl(DBO + "birthDate"), "en")
        .getObject().toString();

    directorModel.setLabel(directorLabel);
    directorModel.setName(name);
    directorModel.setDescription(description);
    directorModel.setBirthDate(birthDate);

    movieModel.setDirector(directorModel);
  }

  /**
   * Adds directed movies to the movie director.
   *
   * @param movieModel the movie data class.
   */
  private static void addDirectedMovies(Movie movieModel) {
    List<Movie> directedMovies = new ArrayList<>();
    String queryString =
        "PREFIX rdfs: <" + RDFS + ">\n" +
            "PREFIX rdf: <" + RDF + ">\n" +
            "PREFIX dbo: <" + DBO + ">\n" +
            "PREFIX dbr: <" + DBR + ">\n" +
            "PREFIX schema: <" + SCHEMA + ">\n" +
            "select ?movie\n" +
            "where {\n" +
            "?movie dbo:director dbr:" + movieModel.getDirector().getLabel() + "\n" +
            "}";

    Query query = QueryFactory.create(queryString);

    QueryExecution qe = QueryExecutionFactory.sparqlService(SPARQL_ENDPOINT, query);
    ResultSet results = qe.execSelect();

    while (results.hasNext()) {
      Movie movie = new Movie();
      QuerySolution querySolution = results.nextSolution();
      Binding binding = ((ResultBinding) querySolution).getBinding();

      String movieName = binding.toString()
          .substring(binding.toString().lastIndexOf("/"), binding.toString().lastIndexOf(">"))
          .replace("/", "")
          .replace("_", " ");

      movie.setName(movieName);
      directedMovies.add(movie);
      movieModel.getDirector().setDirectedMovies(directedMovies);
    }
  }
}
