package server.model;

public class APIStatistics {
  private String URL;
  private String request;
  private Double mean;
  private Integer max;

  public APIStatistics(String URL, String request, Double mean, Integer max) {
    this.URL = URL;
    this.request = request;
    this.mean = mean;
    this.max = max;
  }
}
