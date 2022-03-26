package mk.ukim.finki.movietime.service.impl;

import lombok.RequiredArgsConstructor;
import mk.ukim.finki.movietime.model.Genre;
import mk.ukim.finki.movietime.service.GenreService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFParser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  private static String DATA_URL = "http://dbpedia.org/data/{genre}.ttl";
  private static String RESOURCE_URL = "http://dbpedia.org/resource/{genre}";

  @Override
  public Genre getGenre(String name) {
//    String genreDataUrl = DATA_URL.replace("{genre}", name);
    String genreResourceUrl = RESOURCE_URL.replace("{genre}", name);
    Genre genreModel = new Genre();
    Map<String, String> movies = new HashMap<>();

    Model model = ModelFactory.createDefaultModel();
    RDFParser.source(genreResourceUrl).httpAccept("text/turtle").parse(model.getGraph());

    Resource genreResource = model.getResource(genreResourceUrl);
    createGenreDataModel(name, genreResource, genreModel);
//    InputStream inputStream = FileManager.getInternal().open(genreDataUrl);
//    if (Objects.isNull(inputStream)) {
//      throw new IllegalArgumentException("File not found");
//    }
//    model.read(inputStream, null, "TTL");
//    genreQuery(name, model);

//    System.out.println(genreResource.getProperty(new PropertyImpl("http://www.w3.org/2000/01/rdf-schema#label"), "en")
//        .getObject().toString());
    return null;
  }

  private static void createGenreDataModel(String genreName, Resource genreResource, Genre genreModel) {

  }
  private static void genreQuery(String genreName, Model genreModel) {
//    String queryString =
//        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
//            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
//            "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
//            "SELECT ?name ?description group_concat(distinct ?movie; separator=\", \") as ?movies\n" +
//            "WHERE {\n" +
//            "      dbr:Action_film rdfs:label ?name . FILTER (lang(?name) = \"en\") .\n" +
////            "      {genreName} rdfs:label ?name . FILTER (lang(?name) = \"en\") ." +
//            "      dbr:Action_film dbo:abstract ?description . FILTER (lang(?description) = \"en\") .\n" +
////            "      {genreName} dbo:abstract ?description . FILTER (lang(?description) = \"en\") .\n" +
//            "      dbr:Action_film dbo:wikiPageWikiLink ?movie .\n" +
//            "      ?movie rdf:type dbo:Film .\n " +
//            "}";
    String queryString =
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
            "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
            "PREFIX schema: <http://schema.org/>\n" +
            "select ?name ?description ?movie\n" +
            "where {\n" +
            "dbr:Action_film rdfs:label ?name. FILTER (lang(?name) = \"en\") .\n" +
            "dbr:Action_film dbo:abstract ?description. FILTER (lang(?description) = \"en\") .\n" +
            "dbr:Action_film dbo:wikiPageWikiLink ?movie .\n" +
            "?movie rdf:type dbo:Film .\n" +
            "}";

    System.out.println(queryString);
    Query query = QueryFactory.create(queryString);

    QueryExecution qe = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
//    QueryExecution qe = QueryExecutionFactory.create(query, genreModel);
    ResultSet results = qe.execSelect();

    qe.close();
  }
}
