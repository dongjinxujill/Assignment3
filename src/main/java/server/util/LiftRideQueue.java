package server.util;

import server.dal.LiftRideDao;
import server.model.MyLiftRide;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class LiftRideQueue implements IQueue<MyLiftRide> {
  private LinkedBlockingQueue<MyLiftRide> queue;
  private LiftRideDao liftRideDao;
  private static final int MAXSIZE = 6000;

  public LiftRideQueue() {
    queue = new LinkedBlockingQueue();
    liftRideDao = LiftRideDao.getInstance();
    start();
  }

  public void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          List<MyLiftRide> rides = dequeue(MAXSIZE);
          long start = System.currentTimeMillis();
          try {
            liftRideDao.createLiftRide(rides);
          } catch (SQLException e) {
            e.printStackTrace();
          }
          long diff = System.currentTimeMillis() - start;

          if (diff < 50) {
            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
          }

        }
      }

    }).start();
  }

  @Override
  public void enqueue(MyLiftRide ride) {
    queue.offer(ride);

  }

  @Override
  public List<MyLiftRide> dequeue(int num) {
    List<MyLiftRide> skiers = new ArrayList<>();
    for (int i = 0; i < num && !queue.isEmpty(); i++) {
      skiers.add(queue.remove());
    }
    return skiers;
  }
}
