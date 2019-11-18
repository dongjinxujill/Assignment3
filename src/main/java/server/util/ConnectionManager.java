package server.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

public class ConnectionManager {

//  private static final String URL="jdbc:mysql://34.83.44.106:3306/Upic";
//  private static final String NAME="root";
//  private static final String PASSWORD="Arbsarbs2011!";
//  private static Connection conn = null;
//
//  public static Connection getConnection(){
//    try {
//      Class.forName("com.mysql.cj.jdbc.Driver");
//      return DriverManager.getConnection(URL, NAME, PASSWORD);
//    } catch (ClassNotFoundException | SQLException e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//  public void closeConnection(Connection connection) throws SQLException {
//    try {
//      connection.close();
//    } catch (SQLException e) {
//      e.printStackTrace();
//      throw e;
//    }
//  }

  private static final String DB_USER = "root";
  private static final String DB_PASS = "Arbsarbs2011!";
  private static final String DB_NAME = "Upic";
  private static final String CLOUD_SQL_CONNECTION_NAME = "cs6650-a3:us-west1:myinstance";

  private DataSource pool = null;

  public Connection getConnection() throws SQLException {

    if (pool == null) {
      // The configuration object specifies behaviors for the connection pool.
      HikariConfig config = new HikariConfig();

      // Configure which instance and what database user to connect with.
      config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
      config.setUsername(DB_USER); // e.g. "root", "postgres"
      config.setPassword(DB_PASS); // e.g. "my-password"

      // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
      // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
//      config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
      config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
      config.addDataSourceProperty("useSSL", "false");

      // Initialize the connection pool using the configuration object.
      pool = new HikariDataSource(config);
    }
    return pool.getConnection();
  }

  /** Close the connection to the database instance. */
  public void closeConnection(Connection connection) throws SQLException {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    }
  }

}
