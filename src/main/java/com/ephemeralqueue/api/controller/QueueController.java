package com.ephemeralqueue.api.controller;

import com.ephemeralqueue.engine.queuecollection.QueueCollection;
import com.ephemeralqueue.engine.queuecollection.entities.QueueId;
import com.ephemeralqueue.engine.queuecollection.entities.QueueValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class QueueController {
  private final QueueCollection queueCollection;

  public QueueController() {
    this.queueCollection = new QueueCollection();
    createQueues();
  }

  private void createQueues() {
    for (int i = QueueCollection.DEFAULT_SIZE; i > 0; i--) {
      this.queueCollection.createQueue();
    }
  }

  @PostMapping("/test")
  public boolean test() {
    for (int i = QueueCollection.DEFAULT_SIZE - 1; i >= 0; i--) {
      if (this.queueCollection.get(i).size() < QueueCollection.DEFAULT_SIZE) {
        return false;
      }
    }

    return true;
  }

  @PostMapping("/queue")
  public int create() {
    QueueId id = queueCollection.createQueue();
    return id.id();
  }

  @PostMapping("/queue/{id}/add/{value}")
  public boolean add(@PathVariable int id,
                     @PathVariable int value) {
    return queueCollection.add(id, value);
  }

  @GetMapping("/queue/{id}/poll")
  public Integer poll(@PathVariable int id) {
    return queueCollection.poll(id).value();
  }

  @DeleteMapping("/queue/{id}")
  public void delete(@PathVariable int id) {
    queueCollection.deleteQueue(id);
  }
}