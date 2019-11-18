package client.part2;

public class Record {
  private long startTime;
  private long latency;
  private int responseCode;
  private String requestType;

  public Record(long startTime, String requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public long getLatency() {
    return latency;
  }

  public long getStartTime() {
    return startTime;
  }

  public String getRequestType() {
    return requestType;
  }
}
