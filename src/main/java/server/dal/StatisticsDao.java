package server.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import server.model.APIStatistics;
import server.model.Statistics;
import server.util.ConnectionManager;

public class StatisticsDao {

  protected ConnectionManager connectionManager;
  private static StatisticsDao instance = null;
  protected StatisticsDao() {
    connectionManager = new ConnectionManager();
  }
  public static StatisticsDao getInstance() {
    if(instance == null) {
      instance = new StatisticsDao();
    }
    return instance;
  }

  public List<APIStatistics> getStatistics() throws SQLException {
    Connection conn = null;

    conn = connectionManager.getConnection();

    List<APIStatistics> res = new ArrayList<>();
    String sql = "select * from Statistics";
    Statement statement = null;
    ResultSet results = null;
    try {
      statement = conn.createStatement();
      results = statement.executeQuery(sql);

      while (results.next()) {
        APIStatistics apiStatistics = new APIStatistics(
            results.getString("URL"), results.getString("request"), results.getDouble("mean"), results.getInt("max"));
        res.add(apiStatistics);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(conn != null) {
        conn.close();
      }
      if(statement != null) {
        statement.close();
      }
      if(results != null) {
        results.close();
      }
    }
    return res;
  }

  public void updateStat(List<Statistics> statistics) throws SQLException {
    Connection conn = null;
    ResultSet rs = null;
    conn = connectionManager.getConnection();
    HashMap<Statistics, long[]> statistic = new HashMap<>();
    for(Statistics record : statistics) {
      if (!statistic.containsKey(record)) statistic.put(record, new long[3]);
      long[] arr = statistic.get(record);
      arr[0] += record.getLatency();
      arr[1] ++;
      arr[2] = Math.max(record.getLatency(), arr[2]);
      statistic.put(record, arr);
    }
    String querySql = "select * from Statistics where URL=? and request=?";
    String updateSql = "update Statistics set version=version+1, mean=?, max=?,count=? where URL=? and request=? and version=?";
    PreparedStatement psmt = null;
    for (Map.Entry<Statistics, long[]> entry : statistic.entrySet()) {
      try {
        while (true) {
          psmt = conn.prepareStatement(querySql);
          psmt.setString(1, entry.getKey().getURL());
          psmt.setString(2, entry.getKey().getRequest());
          rs = psmt.executeQuery();
          if (rs.next()) {
            int count = rs.getInt("count");
            int version = rs.getInt("version");
            double mean = rs.getDouble("mean");
            int max = rs.getInt("max");
            mean = (mean * count + entry.getValue()[0]) / (entry.getValue()[1] + count);
            max = (int) Math.max(max, entry.getValue()[2]);
            psmt.close();
            rs.close();
            psmt = conn.prepareStatement(updateSql);
            psmt.setDouble(1, mean);
            psmt.setInt(2, max);
            psmt.setInt(3, (int) (count + entry.getValue()[1]));
            psmt.setString(4, entry.getKey().getURL());
            psmt.setString(5, entry.getKey().getRequest());
            psmt.setInt(6, version);
            int row = psmt.executeUpdate();
            psmt.close();
            psmt = null;
            if (row != 0) break;
          } else {
            String insert = "insert into Statistics values(?,?,?,?,?,?)";
            psmt = conn.prepareStatement(insert);
            psmt.setString(1, entry.getKey().getURL());
            psmt.setString(2, entry.getKey().getRequest());
            psmt.setDouble(3, entry.getValue()[0] * 1.0 / entry.getValue()[1]);
            psmt.setInt(4, (int) entry.getValue()[2]);
            psmt.setInt(5, 1);
            psmt.setInt(6, (int) entry.getValue()[1]);
            int row = psmt.executeUpdate();
            psmt.close();
            psmt = null;
            if (row != 0) break;
          }

        }
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }
    if(conn != null) {
      conn.close();
    }
    if(psmt != null) {
      conn.close();
    }
  }
}
