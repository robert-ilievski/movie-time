package mk.ukim.finki.movietime;

import mk.ukim.finki.movietime.model.Genre;
import mk.ukim.finki.movietime.service.impl.GenreServiceImpl;
import mk.ukim.finki.movietime.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class MovieTimeApplicationTests {

  @InjectMocks
  private GenreServiceImpl genreService;

  @InjectMocks
  private MovieServiceImpl movieService;

  @Test
  public void testGenre() {
    Genre genre = genreService.getGenre("Action_film");
    System.out.println("test");
  }

  @Test
  public void testMovie() {
    Genre genreModel = Genre.builder()
        .name("Action films")
        .description("action films description")
        .movies(new ArrayList<>())
        .build();
  }
}
