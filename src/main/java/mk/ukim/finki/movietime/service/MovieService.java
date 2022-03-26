package mk.ukim.finki.movietime.service;

import mk.ukim.finki.movietime.model.Movie;

public interface MovieService {
  Movie getMovieDetails(String movieName);
}
