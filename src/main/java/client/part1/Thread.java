package client.part1;

import client.part2.Record;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class Thread extends java.lang.Thread implements Runnable {
  private int randomSkierIdStart;
  private int randomSkierIdEnd;
  private int randomTimeStart;
  private int randomTimeEnd;
  private int randomLiftNumStart;
  private int randomLiftNumEnd;
  private int runTimes;
  private Logger logger;
  private int req = 0;
  private int res = 0;
  private int failure = 0;
  private CountDownLatch firstCountDown;
  private CountDownLatch secondCountDown;
  private BlockingQueue<Record> records;
  private SkiersApi apiInstance;
  private static final String SEASONID = "2019";
  private static final int RESORTID = 11;
  private static final String DAYID = "11";

  public Thread(int randomSkierIdStart, int randomSkierIdEnd, int randomTimeStart, int randomTimeEnd,
      int randomLiftNumStart, int randomLiftNumEnd, int runTimes,
      CountDownLatch firstCountDown, CountDownLatch secondCountDown,
      BlockingQueue<Record> records, Logger logger, SkiersApi apiInstance) {
    this.randomSkierIdStart = randomSkierIdStart;
    this.randomSkierIdEnd = randomSkierIdEnd;
    this.randomTimeStart = randomTimeStart;
    this.randomTimeEnd = randomTimeEnd;
    this.randomLiftNumStart = randomLiftNumStart;
    this.randomLiftNumEnd = randomLiftNumEnd;
    this.runTimes = runTimes;
    this.firstCountDown = firstCountDown;
    this.secondCountDown = secondCountDown;
    this.records = records;
    this.logger = logger;
    this.apiInstance = apiInstance;
  }
  private Integer random(int start, int end) {
    return ThreadLocalRandom.current().nextInt(start, end);
  }

  @Override
  public void run() {
    try {
      for (int i = 0; i < runTimes; i++) {
        int liftId = random(randomLiftNumStart, randomLiftNumEnd);
        int time = random(randomTimeStart, randomTimeEnd);
        int skierId = random(randomSkierIdStart, randomSkierIdEnd);
        try {
          long start = System.currentTimeMillis();
          LiftRide liftRide = new LiftRide();
          liftRide.setLiftID(liftId);
          liftRide.setTime(time);
          ApiResponse<Void> api = apiInstance.writeNewLiftRideWithHttpInfo(liftRide, RESORTID, SEASONID, DAYID, skierId);
          if (firstCountDown == null) {
            apiInstance.getSkierDayVerticalWithHttpInfo(RESORTID, SEASONID, DAYID, skierId);
          }
          countReq();
          long latency = System.currentTimeMillis() - start;
          records.add(new Record(start, "POST", latency, api.getStatusCode()));
          if (api.getStatusCode() / 100 == 2) {
            countRes();
          } else {
            countFailure();
            logger.info("Request Fail With Status Code" + api.getStatusCode());
          }
        } catch (Exception e) {
          logger.info("Exception Caught");
          System.out.println(e.getMessage());
        }
      }
    }
    finally {
      if (firstCountDown != null) {
        firstCountDown.countDown();
      }
      secondCountDown.countDown();
    }
  }

  public void countReq() { req++; }

  public void countRes() {
    res++;
  }

  public int getReq() {
    return req;
  }

  public int getRes() {
    return res;
  }

  public void countFailure() {
    failure++;
  }

  public int getFailure() {
    return failure;
  }
}