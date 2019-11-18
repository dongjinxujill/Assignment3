package server.model;

public class Lift {
  private Integer liftId;
  private Integer time;
  private Integer vertical;


  public Integer getLiftId() {
    return liftId;
  }

  public void setLiftId(Integer lift_id) {
    this.liftId = lift_id;
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
