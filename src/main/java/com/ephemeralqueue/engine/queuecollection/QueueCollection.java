package com.ephemeralqueue.engine.queuecollection;

import com.ephemeralqueue.engine.queuecollection.entities.QueueId;
import com.ephemeralqueue.engine.queuecollection.entities.QueueValue;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages a collection of ephemeral queues.
 *
 * Question: Which parts of this implementation are not thread safe?
 *
 * 1. checkIsQueue() - because the queue could not be there, then it could be there but the method still throws exception.
 * 2. getNewQueueId() - this was made thread safe using synchronized.
 * 3. deleteQueue is interesting because it just deletes queues, but something could get put in there afterward.
 *  ... so basically all accessing and managing the queue collection.
 *
 */
public class QueueCollection {
  public static final String                        QUEUE_NOT_FOUND_MESSAGE    = "Queue not found.";
  public static final String                        COLLECTION_IS_FULL_MESSAGE = "Collection is Full.";
  public static final int                           DEFAULT_SIZE               = 1000;
  private             int                           nextQueueId                = 0;

  private final       int queueCapacity;
  private final       Queue<Integer>[]              collection;

  public QueueCollection(int maxCollectionSize, int queueCapacity) {
    this.queueCapacity  = queueCapacity;
//    this.collection     = new ArrayBlockingQueue[maxCollectionSize];
    this.collection     = new ConcurrentLinkedQueue[maxCollectionSize];
  }

  public QueueCollection() {
    this(DEFAULT_SIZE, DEFAULT_SIZE);
  }

  public QueueId createQueue() throws IllegalStateException {
    int i         = getNewQueueId();
//    collection[i] = new ArrayBlockingQueue<>(queueCapacity);
    collection[i] = new ConcurrentLinkedQueue<>();

    return new QueueId(i);
  }

  public void deleteQueue(int queueId) {
    collection[queueId] = null;
  }

  public boolean add(int queueId, int val) throws NoSuchElementException, IllegalStateException {
    checkIsQueue(queueId);
    return collection[queueId].add(val);
  }

  /**
   * It would be confusing if this poll returns a NoSuchElement exception if the queue is empty.
   *
   * Maybe it should just return null like Queue.poll().
   * @param queueId
   * @return
   */
  public QueueValue poll(int queueId) {
    return new QueueValue(
        collection[queueId] == null ? null : collection[queueId].poll()
    );
  }

  private void checkIsQueue(int i) throws NoSuchElementException {
    if (collection[i] == null) {
      throw new NoSuchElementException(QUEUE_NOT_FOUND_MESSAGE);
    }
  }

  private synchronized int getNewQueueId() throws IllegalStateException {
    if (nextQueueId >= collection.length) {
      throw new IllegalStateException(COLLECTION_IS_FULL_MESSAGE);
    }

    return nextQueueId++;
  }

  public Queue<Integer> get(int i) {
    return collection[i];
  }

  public int size() {
    return collection.length;
  }

  public int queueCapacity() {
    return queueCapacity;
  }
}