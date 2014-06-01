package com.alwaysrejoice.worldalive;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alwaysrejoice.worldalive.ai.BaseAI;

public class Life implements Serializable {

  // System stuff (not inherited)
  private static final long serialVersionUID = 1L;
  
  // System Attributes (not inherited)
  private UUID id = null;
  private int x = 0;
  private int y = 0;
  
  // General attributes (inherited) 
  private String name = "";
  private boolean photosynthesis = false;
  private String aiClass = null;
  
  // Instance attributes (not inherited) 
  private double energy = 0.0;
  private double mass = Const.MIN_MASS; 
  private boolean alive = true;
  private int daysDead = 0;
  private int daysAlive = 0;
  private BaseAI aiInstance = null;
  
  // Turn variables (not inherited)
  private double action = 0;  // How much time you have for this turn
  private double stomachContentMass = 0;  

  // Calculated values (not inherited)
  private double radius = 1.0; 
  private double area = 1.0; 
  private double height = 1.0;
  
  // Reproduction (not inherited)
  private List<Life> womb = new ArrayList<Life>();
  private double spawnDistance = 1;
  
  // Display (inherited)
  private String color = "green";
  private String imgFile = null;
  private int imgWidth = 0;
  private String imgClass = "life";
  
  // Fighting  (inherited)
  //  - all these cannot sum more than 1.0  (enforced in LifeUtils.validator)
  //  - Increasing any of these increases your BMR 
  //  - All these must be 0 if photosynthesis is true (enforced in LifeUtils.validator)
  private double attack = 0; // 0-1
  private double defence = 0; // 0-1
  private double stomachSize = 0; // 0-1
  private double metabolism = 0;  // 0-1  How fast you can move (effects action)
  
  // --------------- Constructors and Initialization -----------------------

  /**
   * Conception (not yet in the world)
   */
  public Life(String name, double mass) {
    setId(UUID.randomUUID());
    setName(name);
    setMass(mass);
  }
  
  /**
   * Used when adding a creature to the world
   */
  public Life(String name, int x, int y, double mass) {
    setId(UUID.randomUUID());
    setName(name);
    setXY(x,y);
    setMass(mass);
  }
  
  // ------------------ Misc ---------------------------
  
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
  
  public void addAction(double amount) {
    setAction(action + amount);
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
    // Only do checks on creatures that have been born
    if ((daysAlive > 0) && (newMass < Const.MIN_MASS)) {
      this.mass = 0;
      this.radius = 0;
      this.area = 0;
      this.height = 0;
      if (alive) kill();
    }
    this.mass = newMass;
    this.radius = massToRadius(mass);
    this.area = Math.PI * radius * radius; // area of circle
    this.height = radius;
  }

  public double massToRadius(double mass) {
    // volume of hemisphere
    return Math.pow((mass * 1.5 / Math.PI), 0.3333333); 
  }
  
  public double radiusToMass(double radius) {
    // volume of hemisphere
    return Math.PI * 0.66666 * mass * mass * mass;
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
    String shortId = id.toString();
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
    setAIClass(parent.getAIClass());
    setColor(parent.getColor());
    setImage(parent.getImgFile(), parent.getImgWidth());
    setImgClass(parent.getImgClass());
    setAttack(parent.getAttack());
    setDefence(parent.getDefence());
    setStomachSize(parent.getStomachSize());
    setMetabolism(parent.getMetabolism());
  }
  
  /**
   * Move to the new location
   */
  public void setXY(int inX, int inY) {
    this.x = inX;
    this.y = inY;
  }
  
  public void setImage(String imgFile, int imgWidth) {
    this.imgFile = imgFile;
    this.imgWidth = imgWidth;
  }

  /**
   * Returns the sum of all the fighting variables (total <= 1.0)
   */
  public double getTotalAction() {
    return attack + defence + stomachSize + metabolism;
  }
  
    
  // ---------------- Getters / Setters ------------------

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
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
  
  public String getAIClass() {
    return aiClass;
  }

  public void setAIClass(String aiClass) {
    this.aiClass = aiClass;
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

  public BaseAI getAiInstance() {
    return aiInstance;
  }

  public void setAiInstance(BaseAI aiInstance) {
    this.aiInstance = aiInstance;
  }

  public double getAction() {
    return action;
  }

  public void setAction(double action) {
    this.action = action;
  }

  public double getStomachContentMass() {
    return stomachContentMass;
  }

  public void setStomachContentMass(double stomachContentMass) {
    this.stomachContentMass = stomachContentMass;
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

  public double getSpawnDistance() {
    return spawnDistance;
  }

  public void setSpawnDistance(double spawnDistance) {
    this.spawnDistance = spawnDistance;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getImgFile() {
    return imgFile;
  }
  
  public int getImgWidth() {
    return imgWidth;
  }

  public String getImgClass() {
    return imgClass;
  }

  public void setImgClass(String imgClass) {
    this.imgClass = imgClass;
  }

  public double getAttack() {
    return attack;
  }

  public void setAttack(double attack) {
    this.attack = attack;
  }

  public double getDefence() {
    return defence;
  }

  public void setDefence(double defence) {
    this.defence = defence;
  }

  public double getStomachSize() {
    return stomachSize;
  }

  public void setStomachSize(double stomachSize) {
    this.stomachSize = stomachSize;
  }

  public double getMetabolism() {
    return metabolism;
  }

  public void setMetabolism(double metabolism) {
    this.metabolism = metabolism;
  }

}
