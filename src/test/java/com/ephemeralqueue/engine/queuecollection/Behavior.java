package com.ephemeralqueue.engine.queuecollection;

import com.ephemeralqueue.Shared;
import com.ephemeralqueue.engine.queuecollection.entities.QueueId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import static com.ephemeralqueue.engine.queuecollection.Performance.createQueues;

class Behavior {
  @Test
  public void main() throws InterruptedException {
//    while (true) {
      singleQueue();
      manyQueuesCreationPreThreads();
      collectionFull();
      deleteTwice();
      queueCreationIdsIsMonotonic();
//    }

    // This I was playing with to understand memory.
//    int[][] memory = new int[20_000][20_000];
//
//    while (true) {
//      for (int i = 0; i < 20000; i++) {
//        for (int j = 0; j < 20000; j++) {
//          memory[i][j] = i;
//        }
//      }
//    }
  }

  public static void singleQueue() {
    QueueCollection queueCollection = getQueueCollection();

    QueueId q = queueCollection.createQueue();

    Shared.testCompleteAddAndRemove(queueCollection.get(q.id()), QueueCollection.DEFAULT_SIZE);
  }

  public static void manyQueuesCreationPreThreads() throws InterruptedException {
    QueueCollection queueCollection = getQueueCollection();

    createQueues(QueueCollection.DEFAULT_SIZE, queueCollection);

    List<Thread> threads = new ArrayList<>();

    for (int i = 0; i < QueueCollection.DEFAULT_SIZE; i++) {
      Thread thread = new QueueClient(i, queueCollection);
      threads.add(thread);
    }

    for (Thread thread : threads) {
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    //Delete queues.
    queueCollection.deleteQueue(1);
    queueCollection.deleteQueue(2);

    // Attempt to add after deleting.
    try {
      queueCollection.add(2, new Random().nextInt());
      Shared.assertTrue(false);
    } catch (NoSuchElementException e) {
      Shared.assertTrue(true);
    }

    try {
      queueCollection.add(1, new Random().nextInt());
      Shared.assertTrue(false);
    } catch (NoSuchElementException e) {
      Shared.assertTrue(true);
    }
  }

  static class QueueClient extends Thread {
    QueueCollection queueCollection;
    int queueId;

    QueueClient(int queueId, QueueCollection queueCollection) {
      this.queueId = queueId;
      this.queueCollection = queueCollection;
    }

    public void run() {
      Shared.testCompleteAddAndRemove(queueCollection.get(queueId), QueueCollection.DEFAULT_SIZE);
    }
  }

  public static void collectionFull() {
    QueueCollection queueCollection = getQueueCollection();

//    try {
      for (int i = 0; i < QueueCollection.DEFAULT_SIZE; i++) {
//        long before = Runtime.getRuntime().freeMemory();
        queueCollection.createQueue();
//        System.out.println(before - Runtime.getRuntime().freeMemory());
      }
//    } catch (Exception e) {
//      System.out.println(Runtime.getRuntime().freeMemory());
//    }

    try {
      queueCollection.createQueue();
      Shared.assertTrue(false);
    } catch (IllegalStateException e) {
      Shared.assertTrue(true);
    }
  }

  public static void queueCreationIdsIsMonotonic() {
    QueueCollection queueCollection = getQueueCollection();

    QueueId id1 = queueCollection.createQueue();
    QueueId id2 = queueCollection.createQueue();
    QueueId id3 = queueCollection.createQueue();

    Shared.assertTrue(id1.id() == 0);
    Shared.assertTrue(id2.id() == 1);
    Shared.assertTrue(id3.id() == 2);

    queueCollection.deleteQueue(id3.id());

    QueueId id4 = queueCollection.createQueue();
    QueueId id5 = queueCollection.createQueue();

    Shared.assertTrue(id4.id() == 3);
    Shared.assertTrue(id5.id() == 4);
  }

  public static void deleteTwice() {
    QueueCollection queueCollection = getQueueCollection();

    QueueId q = queueCollection.createQueue();

    queueCollection.deleteQueue(q.id());

    // Try to delete again and nothing should happen.
    queueCollection.deleteQueue(q.id());

    Shared.assertTrue(true);
  }

  private static QueueCollection getQueueCollection() {
    return new QueueCollection();
  }
}