package mk.ukim.finki.movietime.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Director {
  private String name;
  private String description;
  private String birthDate;
  private Map<String, String> movies;
}
