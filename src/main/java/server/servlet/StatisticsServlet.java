package server.servlet;

import server.dal.StatisticsDao;
import server.model.APIStatistics;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "StatisticsServlet")
public class StatisticsServlet extends HttpServlet {

//  StatisticsDao statisticsDao = new StatisticsDao();
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    String urlPath = request.getPathInfo();
    if (urlPath == null || urlPath.isEmpty()) {
      List<APIStatistics> statistics = new ArrayList<>();
      try {
//        statistics = statisticsDao.getStatistics();
      } catch (Exception e) {
        e.printStackTrace();
      }

      String jsonString = new Gson().toJson(statistics);
      response.setStatus(HttpServletResponse.SC_OK);
      out.print(jsonString);
      out.flush();
    }
  }
}
