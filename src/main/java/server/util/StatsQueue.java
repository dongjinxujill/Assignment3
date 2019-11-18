package server.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import server.dal.LiftRideDao;
import server.dal.StatisticsDao;
import server.model.Statistics;

public class StatsQueue implements IQueue<Statistics> {
  private StatisticsDao statisticsDao;
  private LinkedBlockingQueue<Statistics> linkedBlockingQueue;

  public StatsQueue() {
    this.linkedBlockingQueue = new LinkedBlockingQueue<>();
    this.statisticsDao = StatisticsDao.getInstance();
    start();
  }

  private void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
          int size = linkedBlockingQueue.size();
          if (size == 0) continue;
          List<Statistics> statis = dequeue(5000);
          try {
            statisticsDao.updateStat(statis);
          } catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  }

  @Override
  public void enqueue(Statistics statistic) {
    linkedBlockingQueue.offer(statistic);
  }

  @Override
  public List<Statistics> dequeue(int num) {
    List<Statistics> statis = new ArrayList<>();
    for (int i = 0; i < num && !linkedBlockingQueue.isEmpty(); i++) {
      statis.add(linkedBlockingQueue.remove());
    }
    return statis;
  }
}
