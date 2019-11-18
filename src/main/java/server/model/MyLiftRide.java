package server.model;

public class MyLiftRide {
  private Integer skierId;
  private Integer resortId;
  private Integer liftId;
  private String dayId;
  private String seasonId; // 2019
  private Integer time;
  private Integer vertical;

  public MyLiftRide(Integer skierId, Integer resortId, Integer liftId, String dayId,
      String seasonId, Integer time, Integer vertical) {
    this.skierId = skierId;
    this.resortId = resortId;
    this.liftId = liftId;
    this.dayId = dayId;
    this.seasonId = seasonId;
    this.time = time;
    this.vertical = vertical;
  }

  public Integer getSkierId() {
    return skierId;
  }

  public void setSkierId(Integer skierId) {
    this.skierId = skierId;
  }

  public Integer getResortId() {
    return resortId;
  }

  public void setResortId(Integer resortId) {
    this.resortId = resortId;
  }

  public Integer getLiftId() {
    return liftId;
  }

  public void setLiftId(Integer liftId) {
    this.liftId = liftId;
  }

  public String getDayId() {
    return dayId;
  }

  public void setDayId(String dayId) {
    this.dayId = dayId;
  }

  public String getSeasonId() {
    return seasonId;
  }

  public void setSeasonId(String seasonId) {
    this.seasonId = seasonId;
  }

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public Integer getVertical() {
    return vertical;
  }

  public void setVertical(Integer vertical) {
    this.vertical = vertical;
  }
}
