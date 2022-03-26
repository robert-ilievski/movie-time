package mk.ukim.finki.movietime;

import mk.ukim.finki.movietime.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieTimeApplicationTests {

  @InjectMocks
  private GenreServiceImpl genreService;

  @Test
  public void test() {
    genreService.getGenre("Action_film");
  }
}
