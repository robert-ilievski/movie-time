package mk.ukim.finki.movietime.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {
  private String label;
  private String name;
  private String shortDescription;
  private String description;
  private int runtime;
  private Director director;
  private String directorLabel;
  private List<String> starring;
}
