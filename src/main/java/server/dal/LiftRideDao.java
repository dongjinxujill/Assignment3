package server.dal;

import io.swagger.client.model.SkierVertical;
import io.swagger.client.model.SkierVerticalResorts;
import server.model.MyLiftRide;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import server.util.ConnectionManager;

public class LiftRideDao {

  protected ConnectionManager connectionManager;
  private static LiftRideDao instance = null;
  protected LiftRideDao() {
    connectionManager = new ConnectionManager();
  }
  public static LiftRideDao getInstance() {
    if(instance == null) {
      instance = new LiftRideDao();
    }
    return instance;
  }
  public void createLiftRide(List<MyLiftRide> rides) throws SQLException {
    Connection conn = null;

    PreparedStatement psmt = null;
    String sql = "" + "INSERT INTO MyLiftRide" +
        "(skierId,resortId,liftId, dayId,seasonId, time, vertical)" +
        "values(?,?,?,?,?,?,?);";
    try {
      conn = connectionManager.getConnection();
      psmt = conn.prepareStatement(sql);
      for (MyLiftRide ride : rides) {
        psmt.setInt(1, ride.getSkierId());
        psmt.setInt(2, ride.getResortId());
        psmt.setInt(3, ride.getLiftId());
        psmt.setString(4, ride.getDayId());
        psmt.setString(5, ride.getSeasonId());
        psmt.setInt(6, ride.getTime());
        psmt.setInt(7, ride.getVertical());
        psmt.addBatch();
      }
      psmt.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if(conn!= null) {
        conn.close();
      }
      if(psmt != null) {
        psmt.close();
      }
    }
  }


  public Integer getVertical(Integer resortId, String seasonId, String dayId, Integer skierId) throws SQLException{
    String sql = "SELECT SUM(vertical) FROM MyLiftRide WHERE (resortId=?) and (seasonId=?) and (dayId=?) and (skierId=?);";
    Connection con = null;
    PreparedStatement psmt = null;
    ResultSet results = null;

    try {
      con = connectionManager.getConnection();
      psmt = con.prepareStatement(sql);
      psmt.setInt(1, resortId);
      psmt.setString(2, seasonId);
      psmt.setString(3, dayId);
      psmt.setInt(4, skierId);
      results = psmt.executeQuery();

      if(results.next()) {
        return results.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if(con != null) {
        con.close();
      }
      if(psmt != null) {
        con.close();
      }
      if(results != null) {
        results.close();
      }
    }
    return 0;
  }

  public SkierVertical getTotalVertical(int skierId, String[] resortId, String[] seasonId) throws SQLException{
    String sql = "SELECT seasonId, SUM(vertical) FROM MyLiftRide WHERE (skierId=?) and (resortId=?) and (seasonId=?);";
    Connection conn = null;
    PreparedStatement psmt = null;
    ResultSet results = null;

    try {
      conn = connectionManager.getConnection();
      SkierVertical verticalList = new SkierVertical();
      if (seasonId == null) {
        String tmp = "SELECT seasonId FROM MyLiftRide WHERE (skierId=?) and (resortId=?) GROUP BY seasonId;";
        Set<String> set = new HashSet<>();
        for (String s : resortId) {
          psmt = conn.prepareStatement(tmp);
          psmt.setInt(1, skierId);
          psmt.setInt(2, Integer.valueOf(s));
          results = psmt.executeQuery();
          while (results.next()) set.add(results.getString(1));
        }
        seasonId = new String[set.size()];
        seasonId = set.toArray(seasonId);
      }
      for (String s : resortId) {
        for (String value : seasonId) {
          psmt = conn.prepareStatement(sql);
          psmt.setInt(1, skierId);
          psmt.setInt(2, Integer.valueOf(s));
          psmt.setString(3, value);
          results = psmt.executeQuery();
          while (results.next()) {
            int totalVert = results.getInt(2);
            SkierVerticalResorts skierVerticalResorts = new SkierVerticalResorts();
            skierVerticalResorts.setSeasonID(results.getString(1));
            skierVerticalResorts.setTotalVert(totalVert);
            verticalList.addResortsItem(skierVerticalResorts);
          }
        }

      }
      return verticalList;
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if(conn != null) {
        conn.close();
      }
      if(psmt != null) {
        conn.close();
      }
      if(results != null) {
        results.close();
      }
    }

  }
}
