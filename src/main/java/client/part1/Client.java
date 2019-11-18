package client.part1;

import client.part2.Record;
import client.part2.Stats;
import io.swagger.client.ApiClient;
import io.swagger.client.api.SkiersApi;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Client {
  private static final int NUMTHREADS = 256;
  private static final int NUMSKIERS = 20000;
  private static final int NUMLIFTS = 40;
  private static final int NUMRUNS = 20;
  private static final int DEFAULTTHREADMAXCAPACITY = 1000;
  private static final int DEFAULTPOSTMAXCAPACITY = 400000;
  private static final int DIVIDEND = 10;
  //private static final String ip = "http://ec2-54-175-241-239.compute-1.amazonaws.com";
//  private static final String ip = "http://localhost";
//  private static final String port = "8080";
  private static BlockingQueue<Thread> phases = new ArrayBlockingQueue<>(DEFAULTTHREADMAXCAPACITY);
  private static int numReq = 0;
  private static int numRes = 0;
  private static int numFailure = 0;
  private static BlockingQueue<Record> records = new ArrayBlockingQueue<>(DEFAULTPOSTMAXCAPACITY);
  private static Logger logger = Logger.getLogger(Thread.class.getName());
  private static String basePath;
  private static ApiClient client;

  public static void main(String[] arg) throws Exception {
    int p1Threads = NUMTHREADS / 4;
    int p2Threads = NUMTHREADS;
    CountDownLatch p1 = new CountDownLatch((int) (p1Threads * 0.1));
    CountDownLatch p2 = new CountDownLatch((int) (p2Threads * 0.1 ));

    //basePath = ip + ":" + port + "/assignment1";
//    basePath = ip + ":" + port;
    basePath = "https://cs6650-a3.appspot.com";

    SkiersApi apiInstance = new SkiersApi();
    client = apiInstance.getApiClient();
    client.setBasePath(basePath);

    long wallStart = System.currentTimeMillis();
    //------------------phase1
    CountDownLatch p1Monitor = new CountDownLatch(NUMTHREADS/4);
    phase1(p1, p1Monitor, apiInstance);

    //-------------------phase2
    CountDownLatch p2Monitor = new CountDownLatch(NUMTHREADS);
    phase2(p2, p2Monitor, apiInstance);

    //--------------------phase3
    CountDownLatch p3Monitor = new CountDownLatch(NUMTHREADS/4);
    phase3(null ,p3Monitor, apiInstance);

    try {
      p1Monitor.await();
      p2Monitor.await();
      p3Monitor.await();
    } catch (InterruptedException e) {

    }
    long wallEnd = System.currentTimeMillis();

    counter();

    Stats stats = new Stats(wallEnd - wallStart, numReq,
        numRes, numFailure, records, logger);
    stats.performanceStats();
  }

  private static void counter() {
    for (Thread thread : phases) {
      numFailure += thread.getFailure();
      numReq += thread.getReq();
      numRes += thread.getRes();
    }
  }

  private static void phase1(CountDownLatch firstCountDown, CountDownLatch secondCountDown, SkiersApi apiInstance) {
    int skierIdRange = NUMSKIERS/(NUMTHREADS/4);
    int endTime = 90;

    ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS/4);
    int runTimes = NUMRUNS/DIVIDEND*skierIdRange;
    for (int i = 0; i < NUMTHREADS/4; i++) {
      Thread thread = new Thread(
          skierIdRange*i,
              skierIdRange*i + skierIdRange,
          0, endTime,
          0, NUMLIFTS,
          runTimes, firstCountDown, secondCountDown, records, logger, apiInstance);
      executorService.execute(thread);
      phases.add(thread);
    }
    executorService.shutdown();
    try {
      firstCountDown.await();
    } catch (InterruptedException e) {
      System.out.println("phase 1 cd error");
    }
  }

  private static void phase2(CountDownLatch firstCountDown, CountDownLatch secondCountDown, SkiersApi apiInstance) {
    int startTime = 91;
    int endTime = 360;
    int skierIdRange = NUMSKIERS / NUMTHREADS;
    int runTimes = (int) (NUMRUNS * 0.8) * skierIdRange;
    ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS);
    for (int i = 0; i < NUMTHREADS; i++) {
      Thread thread = new Thread(
            skierIdRange * i,
              skierIdRange * i + skierIdRange,
          startTime, endTime, 0, NUMLIFTS + 1,
          runTimes, firstCountDown, secondCountDown, records, logger, apiInstance);
      executorService.execute(thread);
      phases.add(thread);
    }
    executorService.shutdown();
    try {
      firstCountDown.await();
    } catch (InterruptedException e) {
      System.out.println("phase 2 cd error");
    }
  }

  private static void phase3(CountDownLatch firstCountDown, CountDownLatch secondCountDown, SkiersApi apiInstance) {
    int startTime = 361;
    int endTime = 420;
    int skierIdRange = NUMSKIERS/(NUMTHREADS/4);
    int runTimes = NUMRUNS/DIVIDEND;
    ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS/4);
    for (int i = 0; i < NUMTHREADS/4; i++) {
      Thread thread = new Thread(
          skierIdRange*i,
              skierIdRange*i + skierIdRange,
          startTime, endTime, 0, NUMLIFTS,
          runTimes, firstCountDown, secondCountDown, records, logger, apiInstance);
      executorService.execute(thread);
      phases.add(thread);
    }
    executorService.shutdown();
  }
}

