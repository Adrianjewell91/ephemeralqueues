package com.ephemeralqueue.api.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueControllerTest {
  private QueueController queueController;

  @BeforeEach
  void setUp() {
    queueController = new QueueController();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void create() {
    queueController.create();
  }

  @Test
  void add() {
    queueController.create();
    queueController.add(0, 1);
  }

  @Test
  void poll() {
    queueController.create();
    queueController.add(0, 1);
    queueController.poll(0);
  }

  @Test
  void delete() {
    queueController.create();
    queueController.add(0, 1);
    queueController.poll(0);
    queueController.delete(0);
  }
}