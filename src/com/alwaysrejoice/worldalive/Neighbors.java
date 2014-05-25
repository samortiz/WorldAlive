package com.alwaysrejoice.worldalive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Neighbors {

  private List<Life> neighbors = new ArrayList<Life>();
  private HashMap<Life, Double> distances = new HashMap<Life, Double>();
  private double totalMass = 0.0;
  
  
  /**
   * Constructor
   */
  public Neighbors() {
  }
  
  /**
   * Adds a neighbor
   */
  public void addNeighbor(Life life, double distance) {
    neighbors.add(life);
    distances.put(life, distance);
    totalMass += life.getMass();
  }
  
  /**
   * Adds a life to the neihborhood if it's as close or closer than distance
   */
  public void addNeighborIfNearby(Life life, int x, int y, double distance) {
    int xDistance = life.getX() - x;
    int yDistance = life.getY() - y;
    double lDistance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    if (lDistance <= distance) {
      addNeighbor(life, lDistance);
    }
  }  

  /**
   * Mass of all the neighbors summed up
   */
  public double getTotalMass() {
    return totalMass;
  }

  /** 
   * Gets the distance of the specified life
   */
  public double getDistance(Life him) {
    return distances.get(him);
  }
  
  /** 
   * The list of neighbors
   */
  public List<Life> getNeighbors() {
    return neighbors;
  }
  
  /** 
   * Display a list of neighbors
   */
  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("(");
    for (Life life : neighbors) {
      str.append(life.getShortId()+" ");
    }
    if (neighbors.size() > 0) str.deleteCharAt(str.length()-1);
    str.append(")");
    return str.toString();
  }
  
}
