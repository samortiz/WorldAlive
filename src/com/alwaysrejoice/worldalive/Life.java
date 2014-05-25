package com.alwaysrejoice.worldalive;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Life implements Serializable {

  // System stuff
  private static final long serialVersionUID = 1L;
  private static long lastId = 0; // stores the last generated ID
  
  // System Attributes (not inherited)
  private long id = 0;
  private int x = 0;
  private int y = 0;
  
  // General attributes (inherited) 
  private String name = "";
  private boolean photosynthesis = false;
  private double stomachSize = 0.2; // percent of mass

  // Reproduction (inherited)
  private int spawnDistance = 2; // distance offspring are born
  private double birthMass = Const.MIN_MASS; // size of baby when born
  private double massToFetus = 1; // mass to each fetus each turn
  private double litterSize = 1; // Max number of fetus in womb at one time
  private double massToStartReproducing = 100; // when to start reproducing
  
  // Instance attributes (not inherited) 
  private double energy = 0.0;
  private double mass = Const.MIN_MASS; 
  private boolean alive = true;
  private int daysDead = 0;
  private int daysAlive = 0;

  // Calculated values (not inherited)
  private double radius = 1.0; 
  private double area = 1.0; 
  private double height = 1.0;
  
  // Reproduction
  private List<Life> womb = new ArrayList<Life>();

  // Display (inherited)
  private String color = "green";
  
  // --------------- Constructors and Initialization -----------------------

  /**
   * Conception (not yet in the world)
   */
  public Life(String name, double mass) {
    setId(newId());
    setName(name);
    setMass(mass);
  }
  
  /**
   * Used when adding a creature to the world
   */
  public Life(String name, int x, int y, double mass) {
    setId(newId());
    setName(name);
    moveTo(x,y);
    setMass(mass);
  }
  
  /**
   * Generate a new ID, try to make this unique
   */
  public synchronized long newId() {
    long newId = System.currentTimeMillis();
    while (newId == Life.lastId) {
      newId = System.currentTimeMillis();
    }
    Life.lastId = newId;
    return newId;
  }
   
  // ------------------ Misc ---------------------------
  
  @Override
  public String toString() {
    DecimalFormat f = new DecimalFormat("0.#");
    StringBuffer display = new StringBuffer(
       getShortId()+" ("+x+","+y+") "+name+" "+
       daysAlive+(photosynthesis ? "P " : "A ") +
       f.format(mass)+"g "+
       f.format(area)+"a "+
       f.format(radius)+"r "+
       f.format(height)+"h "+
       f.format(energy)+"j "+
        "");
    display.append("(");
    for (Life child : womb) {
      display.append(f.format(child.getMass())+"g ");
    }
    if (womb.size() > 0) display.deleteCharAt(display.length()-1);
    display.append(")");
    
    return display.toString();
  }

  public void addMass(double amount) {
    setMass(mass + amount);
  }
  
  /**
   * Add or Remove energy from this life
   * If the energy falls below zero it will start consuming its mass
   */
  public void addEnergy(double amount) {
    double newEnergy = energy + amount;
    
    // A shortage of energy means you're starving
    // so you start eating yourself
    if (newEnergy < 0) {
      addMass(newEnergy * Const.MASS_TO_ENERGY);
      newEnergy = 0;
    }
    
    this.energy = newEnergy;
  }
  
  /**
   * Grow! Converts energy to mass increasing your size
   */
  public void energyToMass(double amount) {
    // Verify the life has enough energy to do this
    double actualAmount = amount;
    if (amount > energy) {
      actualAmount = energy;
    }
    addMass(actualAmount * Const.ENERGY_TO_MASS);
    addEnergy(-actualAmount);
  }
  
  /**
   * Sets the mass and recalculates the other dependent variables
   */
  public void setMass(double newMass) {
    if (newMass < Const.MIN_MASS) {
      this.mass = 0;
      this.radius = 0;
      this.area = 0;
      this.height = 0;
      if (alive) kill();
    }
    this.mass = newMass;
    this.radius = Math.pow((mass * 1.5 / Math.PI), 0.3333333); // volume of hemisphere
    this.area = Math.PI * radius * radius; // area of circle
    this.height = radius;
  }

  /** 
   * Kills this creature.  
   */
  public void kill() {
    if (alive) {
      this.alive = false;
      this.daysDead = 0;
      throw new DeathException(getShortId()+" a "+name+" has died after "+daysAlive+" days");
    }
  }
  
  /**
   * Determines whether this critter is gone and can be removed from the world
   */
  public boolean isGone() {
    return !alive && mass < Const.MIN_MASS;
  }

  /**
   * Short version of the ID
   */
  public String getShortId() {
    String shortId = Long.toString(id);
    return shortId.substring(shortId.length()-4);
  }
  
  /**
   * Sets all the inheritable characteristics of this life from 
   * the specified parent Life
   */
  public void inheritFrom(Life parent) {
    setName(parent.getName());
    setPhotosynthesis(parent.getPhotosynthesis());
    setStomachSize(parent.getStomachSize());
    setSpawnDistance(parent.getSpawnDistance());
    setBirthMass(parent.getBirthMass());
    setMassToFetus(parent.getMassToFetus());
    setLitterSize(parent.getLitterSize());
    setMassToStartReproducing(parent.getMassToStartReproducing());
    setColor(parent.getColor());
  }
  
  /**
   * Move to the new location
   * Note : the borders will be desirable for plants as there is no competition in the void! 
   */
  public void moveTo(int inX, int inY) {
    int newX = inX;
    int newY = inY;
    // Check the boundaries
    if (newX < 0) newX = Const.MAX_X - newX;
    if (newY < 0) newY = Const.MAX_Y - newY;
    if (newX > Const.MAX_X) newX = Const.MAX_X - newX;
    if (newY > Const.MAX_Y) newY = Const.MAX_Y - newY;
    
    this.x = newX;
    this.y = newY;
  }
  
    
  // ---------------- Getters / Setters ------------------

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public boolean getPhotosynthesis() {
    return photosynthesis;
  }

  public void setPhotosynthesis(boolean photosynthesis) {
    this.photosynthesis = photosynthesis;
  }
  
  public double getStomachSize() {
    return stomachSize;
  }

  public void setStomachSize(double stomachSize) {
    this.stomachSize = stomachSize;
  }
  
  public int getSpawnDistance() {
    return spawnDistance;
  }
  
  public void setSpawnDistance(int spawnDistance) {
    this.spawnDistance = spawnDistance;
  }

  public double getBirthMass() {
    return birthMass;
  }
  
  public void setBirthMass(double birthMass) {
    this.birthMass = birthMass;
  }

  public double getMassToFetus() {
    return massToFetus;
  }

  public void setMassToFetus(double massToFetus) {
    this.massToFetus = massToFetus;
  }

  public double getLitterSize() {
    return litterSize;
  }

  public void setLitterSize(double litterSize) {
    this.litterSize = litterSize;
  }

  public double getMassToStartReproducing() {
    return massToStartReproducing;
  }

  public void setMassToStartReproducing(double massToStartReproducing) {
    this.massToStartReproducing = massToStartReproducing;
  }

  public double getEnergy() {
    return energy;
  }

  public double getMass() {
    return mass;
  }
  
  public boolean isAlive() {
    return alive;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  public int getDaysDead() {
    return daysDead;
  }

  public void setDaysDead(int daysDead) {
    this.daysDead = daysDead;
  }

  
  public int getDaysAlive() {
    return daysAlive;
  }

  public void setDaysAlive(int daysAlive) {
    this.daysAlive = daysAlive;
  }

  public double getRadius() {
    return radius;
  }
  
  public double getArea() {
    return area;
  }

  public double getHeight() {
    return height;
  }


  public List<Life> getWomb() {
    return womb;
  }

  public void setWomb(List<Life> womb) {
    this.womb = womb;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  
}
