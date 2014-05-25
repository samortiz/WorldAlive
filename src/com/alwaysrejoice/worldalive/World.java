package com.alwaysrejoice.worldalive;

import java.util.ArrayList;
import java.util.List;

public class World {
  // The whole world
  private static World world = null;
 
  private List<Life> lives = new ArrayList<Life>();
  private List<Life> births = new ArrayList<Life>();
  
  
  /**
   * Do not create instances, use the getWorld() method
   */
  private World() { 
  }
  
  public static World getWorld() {
    if (world == null) {
      world = new World();
      WorldIO.load(world);
    }
    return world;
  }
  
  /**
   * Adds a Life to the birth-queue of the world
   * The life will be born on the next free cycle
   * @param l Life to be born
   */
  public void addBirth(Life life) {
    births.add(life);
  }

  /**
   * Adds a life to the world. 
   */
  public void addLife(Life baby) {
    // Find an empty spot in the array
    for (int i=0; i<lives.size(); i++) {
      Life life = lives.get(i);
      if (life.isGone()) {
        lives.set(i, baby);
        return;
      }
    }
    // Nobody is gone so add to the end
    lives.add(baby);
  }
  
  /**
   * Returns a list of all the neighbours within distance of (x,y)
   */
  public Neighbors getNeighbors(int x, int y, int distance) {
    Neighbors neighbors = new Neighbors();
    int minX = x - distance;
    int maxX = x + distance;
    int minY = y - distance;
    int maxY = y + distance;
    for (Life life : lives) {
      if (life.isGone()) continue;
      
      // Check the bounding box (short circuit calculation) 
      int lX = life.getX();
      if ((lX < minX) || (lX > maxX)) continue;
      int lY = life.getY();
      if ((lY < minY) || (lY > maxY)) continue;
      
      // The life is within a bounding box
      // so lets actually calculate the distance (more expensive)
      neighbors.addNeighborIfNearby(life, x,y, distance);
    }
    return neighbors;
  }
 
  /**
   * Returns a list of all the neighbours that overlap (by radius) 
   * This depends on your radius and the radius of everyone else! 
   */
  public Neighbors getNeighbors(Life me) {
    Neighbors neighbors = new Neighbors();
    int x = me.getX();
    int y = me.getY();
    for (Life life : lives) {
      // exclude yourself and dearly departed
      if (life.isGone() || (life == me)) continue;
     
      // Overlap Distance (my radius + your radius)
      double distance = me.getRadius() + life.getRadius();
      
      // Check the bounding box (short circuit calculation) 
      int lX = life.getX();
      if ((lX < (x - distance)) || (lX > (x + distance))) continue;
      int lY = life.getY();
      if ((lY < (y - distance)) || (lY > (y + distance))) continue;
      
      // The life is within a bounding box
      // so lets actually calculate the distance (more expensive)
      neighbors.addNeighborIfNearby(life, x,y, distance);
    }
    return neighbors;
  }
  
  
  
  // ----------------- Getters / Setters -----------------
  
  public List<Life> getLives() {
    return lives;
  }

  public List<Life> getBirths() {
    return births;
  }
}
