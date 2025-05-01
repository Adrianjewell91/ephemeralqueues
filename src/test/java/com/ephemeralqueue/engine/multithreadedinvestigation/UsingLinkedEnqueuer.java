package com.ephemeralqueue.engine.multithreadedinvestigation;

import com.ephemeralqueue.engine.queue.RingBufferQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * I learned from this test that my implementation of
 * Ephemeral Queue is not thread safe, but I would like it be.
 *
 * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentLinkedQueue.html
 * https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html
 */
public class UsingLinkedEnqueuer {
  private static final int QUEUE_LENGTH = 100_000;
  private static final List<Integer> result = new ArrayList<>();

  @Test
  public void main() {
    usingConcurrentLinkedEnqueuer();
  }

  /**
   * In this strategy, the two threads add values to the concurrent queue in a thread safe way, then
   * it is enqueue to the ephemeral queue in a single thread.
   */
  private static void usingConcurrentLinkedEnqueuer() {
    Queue<Integer> q = new RingBufferQueue(QUEUE_LENGTH);
    ConcurrentLinkedQueue<Integer> enqueues = new ConcurrentLinkedQueue<Integer>();

    LinearizedEnqueuer t1 = new LinearizedEnqueuer(enqueues, 1);
    LinearizedEnqueuer t2 = new LinearizedEnqueuer(enqueues, 2000);

    t1.start();
    t2.start();

    int k = 0;
    do {
      // It could return null but keep trying.
      Integer value = enqueues.poll();
      if (value != null) {
        q.add(value);
        k++;
      }

    } while (k < 40);

    for (int i = 0; i < 40; i++) {
      result.add(q.poll());
    }

    System.out.println(result);
  }

  private static class LinearizedEnqueuer extends Thread {
    Queue<Integer> queue;
    int multiplier;

    LinearizedEnqueuer(Queue<Integer> queue, int multiplier) {
      this.queue = queue;
      this.multiplier = multiplier;
    }

    public void run() {
      for (int i = 0; i < 2000; i++) {
        queue.add(i * multiplier);
      }
    }
  }
}