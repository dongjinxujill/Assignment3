package server.model;

public class Resort {
  private Integer resortId;
  private String seasonId;
  private String resortName;

  public Integer getResortId() {
    return resortId;
  }

  public void setResortId(Integer resortId) {
    this.resortId = resortId;
  }

  public String getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(String seasonId) {
    this.seasonId = seasonId;
  }

  public String getResortName() {
    return resortName;
  }

  public void setResortName(String resortName) {
    this.resortName = resortName;
  }
}

