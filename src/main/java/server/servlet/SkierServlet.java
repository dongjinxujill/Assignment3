package server.servlet;

import javax.servlet.ServletException;
import server.util.StatsQueue;
import server.model.Statistics;
import server.util.IQueue;
import server.util.LiftRideQueue;
import server.dal.LiftRideDao;
import server.model.MyLiftRide;
import com.google.gson.Gson;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.SkierVertical;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  private int URL_BY_DAY_SIZE = 8;
  private int URL_BY_RESORT_SIZE = 3;
  private LiftRideDao liftRideDao = LiftRideDao.getInstance();
  IQueue<MyLiftRide> queueService = new LiftRideQueue();
//  IQueue<Statistics> statQueueService = new StatsQueue();

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    // check we have a URL!`
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (!isGetVerByDayOrPost(urlParts)) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      String jsonString = new Gson().toJson("Input Not Valid");
      out.write(jsonString);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
      BufferedReader buffIn = request.getReader();
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = buffIn.readLine()) != null) {
        sb.append(line);
      }
      LiftRide liftRide = new Gson().fromJson(sb.toString(), LiftRide.class);
      MyLiftRide ride = getRideInfo(urlParts, liftRide);
      try {
        long wallStart = System.currentTimeMillis();
//        queueService.enqueue(ride);
//        long latency = System.currentTimeMillis() - wallStart;;
//        statQueueService.enqueue(new Statistics("/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}", "POST", latency));

      } catch (Exception e) {
        e.printStackTrace();
      }
      String jsonString = new Gson().toJson("new ride created");
      response.getWriter().write(jsonString);
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();
    // check we have a URL!`
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      String jsonString = new Gson().toJson("missing paramterers");
      out.write(jsonString);
      return;
    }

    String[] urlParts = urlPath.split("/");

    if (isGetVerByDayOrPost(urlParts)) {
      response.setStatus(HttpServletResponse.SC_OK);
      Integer resortId = Integer.valueOf(urlParts[1]);
      String seasonId = urlParts[3];
      String dayId = urlParts[5];
      Integer skierId = Integer.valueOf(urlParts[7]);
      try {
        long wallStart = System.currentTimeMillis();
        Integer vertical = liftRideDao.getVertical(resortId, seasonId, dayId, skierId);
        long latency = System.currentTimeMillis() - wallStart;
//        statQueueService.enqueue(new Statistics("/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}", "GET", latency));
        String jsonString = new Gson().toJson(vertical);
        out.write(jsonString);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (isGetVerByRes(urlParts)) {
      SkierVertical skierVertical = null;
      Integer skierId = Integer.valueOf(urlParts[1]);
      String[] resortId = request.getParameterValues("resort");
      if (resortId == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String jsonString = new Gson().toJson("missing specified resort");
        out.write(jsonString);
        return;
      }
      String[] seasonId = request.getParameterValues("season");
      try {
        long wallStart = System.currentTimeMillis();
        skierVertical = liftRideDao.getTotalVertical(skierId, resortId, seasonId);
        long latency = System.currentTimeMillis() - wallStart;
//        statQueueService.enqueue(new Statistics("/{skierID}/vertical", "GET", latency));
        String jsonString = new Gson().toJson(skierVertical);
        out.write(jsonString);
      } catch (Exception e) {
        e.printStackTrace();
      }
      response.setStatus(HttpServletResponse.SC_OK);
      String jsonString = new Gson().toJson(skierVertical);
      out.write(jsonString);
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private MyLiftRide getRideInfo(String[] url, LiftRide liftRide ) throws IOException{
    Integer resortId = Integer.valueOf(url[1]);
    String seasonId = url[3];
    String dayId = url[5];
    Integer skierId = Integer.valueOf(url[7]);
    Integer liftId = liftRide.getLiftID();
    Integer time = liftRide.getTime();
    Integer vertical  = liftId * 10;
    return new MyLiftRide(skierId, resortId, liftId, dayId, seasonId, time, vertical);
  }

  private boolean isGetVerByDayOrPost(String[] urlPath) {
    // urlPath  = "/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}"
    // http://localhost:8090/skiers/1/seasons/3/days/4/skiers/5
    // urlParts = [, 1, seasons, 2019, days, 1, skiers, 123]
    return urlPath.length == URL_BY_DAY_SIZE && urlPath[2].equals("seasons")
        && urlPath[4].equals("days")  && urlPath[6].equals("skiers");
  }

  private boolean isGetVerByRes(String[] urlPath) {
    // urlPath  = "/{skierID}/vertical"
    //http://localhost:8090/skiers/1/vertical
    return urlPath.length == URL_BY_RESORT_SIZE && urlPath[2].equals("vertical");
  }

}
