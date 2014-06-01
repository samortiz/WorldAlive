package com.alwaysrejoice.worldalive.ai;

public class ElkAI extends HerbivoreAI {

  private static final long serialVersionUID = 1L;

  public ElkAI() {
    this.massToStartReproducing = 500;
    this.litterSize = 1;
    this.spawnDistance = 1;
    this.massToFetus = 50;
    this.birthMass = 300;
    this.minMass = 250;
  }
  
}
