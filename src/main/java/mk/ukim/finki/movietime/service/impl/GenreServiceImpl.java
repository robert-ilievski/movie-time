package mk.ukim.finki.movietime.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.movietime.model.Genre;
import mk.ukim.finki.movietime.model.Movie;
import mk.ukim.finki.movietime.service.GenreService;
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
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mk.ukim.finki.movietime.model.prefixes.Prefixes.DBR;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.RDF;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.RESOURCE_URL;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.RDFS;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.DBO;
import static mk.ukim.finki.movietime.model.prefixes.Prefixes.SCHEMA;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  @Override
  public Genre getGenre(String name) {
    String genreResourceUrl = RESOURCE_URL.replace("{resourceName}", name);
    Genre genreModel = new Genre();

    Model model = ModelFactory.createDefaultModel();
    RDFParser.source(genreResourceUrl).httpAccept("text/turtle").parse(model.getGraph());

    Resource genreResource = model.getResource(genreResourceUrl);
    createGenreDataModel(genreResource, genreModel);
    addMoviesToGenre(name, genreModel);

    return genreModel;
  }

  /**
   * Adds name and description to the given {@link Genre} model.
   *
   * @param genreResource genre resource.
   * @param genreModel    genre model class.
   */
  private static void createGenreDataModel(Resource genreResource, Genre genreModel) {
    genreModel.setName(genreResource.getProperty(
        new PropertyImpl(RDFS + "label"), "en")
        .getObject().toString().replace("@en", "s"));
    genreModel.setDescription(genreResource.getProperty(
        new PropertyImpl(DBO + "abstract"), "en")
        .getObject().toString().replace("@en", ""));
  }

  /**
   * Adds movies containing label, name and short description to the given {@link Genre} model
   * by executing a SPARQL query.
   *
   * @param genreName  genre name.
   * @param genreModel genre model class.
   */
  private static void addMoviesToGenre(String genreName, Genre genreModel) {
    List<Movie> movies = new ArrayList<>();
    String queryString =
        "PREFIX rdfs: <" + RDFS + ">\n" +
            "PREFIX rdf: <" + RDF + ">\n" +
            "PREFIX dbo: <" + DBO + ">\n" +
            "PREFIX dbr: <" + DBR + ">\n" +
            "PREFIX schema: <" + SCHEMA + ">\n" +
            "select ?movie ?shortDescription\n" +
            "where {\n" +
            "dbr:" + genreName + " dbo:wikiPageWikiLink ?movie .\n" +
            "?movie rdf:type dbo:Film .\n" +
            "?movie rdfs:comment ?shortDescription . FILTER (lang(?shortDescription) = \"en\")\n" +
            "}";

    Query query = QueryFactory.create(queryString);

    QueryExecution qe = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
    ResultSet results = qe.execSelect();

    while (results.hasNext()) {
      QuerySolution querySolution = results.nextSolution();
      Binding binding = ((ResultBinding) querySolution).getBinding();

      Movie movie = new Movie();
      Iterator<Var> varIterator = binding.vars();
      while (varIterator.hasNext()) {
        Var queryColumn = varIterator.next();
        String queryColumnValue = binding.get(queryColumn).toString();
        if (queryColumnValue.contains("http://dbpedia.org/resource/")) {
          String movieLabel = queryColumnValue.substring(queryColumnValue.lastIndexOf("/") + 1);
          String movieName = movieLabel.replace("_", " ");

          movie.setLabel(movieLabel);
          movie.setName(movieName);
        } else {
          movie.setShortDescription(queryColumnValue.replace("@en", ""));
        }
      }
      movies.add(movie);
    }

    genreModel.setMovies(movies);
    qe.close();
  }
}
