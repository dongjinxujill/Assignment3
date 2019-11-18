package server.model;

public class Statistics {
  private String URL;
  private String request;
  private long latency;

  public Statistics(String URL, String request, long latency) {
    this.URL = URL;
    this.request = request;
    this.latency = latency;
  }

  public String getURL() {
    return URL;
  }

  public void setURL(String URL) {
    this.URL = URL;
  }

  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }

  public long getLatency() {
    return latency;
  }

  public void setLatency(long latency) {
    this.latency = latency;
  }
}
