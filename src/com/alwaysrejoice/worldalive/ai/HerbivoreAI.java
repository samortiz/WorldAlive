package com.alwaysrejoice.worldalive.ai;

import java.util.List;

public class HerbivoreAI extends BaseAI {
  
  public HerbivoreAI() {
    this.massToStartReproducing = 6000;
    this.litterSize = 1;
    this.spawnDistance = 1;
    this.massToFetus = 100;
    this.birthMass = 1000;
  }
  
  
  public void run() {
    List<LifeI> neighbors = me.getNeighbors();
    for (int i=0; i<neighbors.size(); i++) {
      LifeI neighbor = neighbors.get(i);
      if (neighbor.getMass() < me.getMass()) {
        me.attack(i);
      }
    }// for
    
    int newX = me.getX() + random.nextInt(11)-5;
    int newY = me.getY() + random.nextInt(11)-5;
    me.moveTo(newX, newY);
    
    reproduce();
    convertExtraEnergyToMass();
  }
  
}
