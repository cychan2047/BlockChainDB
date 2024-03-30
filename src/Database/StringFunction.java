package Database;

/* Class representing a data file
 * Size of record: 64 bytes
 * Key: movieId (4 bytes)
 * Other fields: title, genres (variable length)
 * Truncate the title and genres to let the
 * serialized record fit into 64 bytes
 */

public class StringFunction {

  private static final int RECORD_SIZE = 64;

  // UTF-16 encoding by default takes 2 bytes per character
  private static final int CHAR_SIZE = 2;

  // Use "\n" as the end of string indicator in the serialized record
  private static final int END_OF_STRING_SIZE = 2;

  private String movieId; // size = number of digits * CHAR_SIZE
  private final int movieIDInt;

  private String title; // size = length * CHAR_SIZE

  private String genres; // size = length * CHAR_SIZE

  public StringFunction(String readline) {
    String[] data = readline.split(",");
    this.movieId = data[0];
    this.movieIDInt = Integer.parseInt(data[0]);
    if (CHAR_SIZE * movieId.length() +
        CHAR_SIZE * data[1].length() +
        END_OF_STRING_SIZE >
        RECORD_SIZE) {
      this.title = data[1].substring(0,
          (RECORD_SIZE -
              CHAR_SIZE * movieId.length() -
              END_OF_STRING_SIZE)
              / CHAR_SIZE);
    } else {
      this.title = data[1];
    }
    if (CHAR_SIZE * movieId.length() +
        CHAR_SIZE * this.title.length() +
        CHAR_SIZE * data[2].length() +
        END_OF_STRING_SIZE >
        RECORD_SIZE) {
      this.genres = data[2].substring(0,
          (RECORD_SIZE -
              CHAR_SIZE * movieId.length() -
              CHAR_SIZE * title.length() -
              END_OF_STRING_SIZE)
              / CHAR_SIZE);
    } else {
      this.genres = data[2];
    }
  }

  // Serialize the record to a string
  public String toString() {
    return movieId + "," + title + "," + genres + "\n";
  }

  public String getMovieId() {
    return movieId;
  }

  public void setMovieId(String movieId) {
    this.movieId = movieId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getGenres() {
    return genres;
  }

  public void setGenres(String genres) {
    this.genres = genres;
  }

  public int getRecordSize() {
    return RECORD_SIZE;
  }

  public int getMovieIDInt() {
    return movieIDInt;
  }
}
