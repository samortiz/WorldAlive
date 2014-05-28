package com.alwaysrejoice.worldalive.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alwaysrejoice.worldalive.Const;
import com.alwaysrejoice.worldalive.DeathException;
import com.alwaysrejoice.worldalive.Life;
import com.alwaysrejoice.worldalive.Neighbors;
import com.alwaysrejoice.worldalive.World;

public class LifeI {

  // Current life's instance
  private Life life = null;
  protected Random random = new Random();
  private Neighbors neighbors = null;
  
  // ----------------- Constructors --------------------------

  // Don't call this
  @SuppressWarnings("unused")
  private LifeI() {}
  
  /**
   * Constructor
   */
  public LifeI(Life life) {
    this.life = life;
  }
  
  // ------------------ Misc Methods ------------------------------
  
  public String toString() {
    return life.toString();
  }
  
  /**
   * Grow! Converts energy to mass increasing your size
   */
  public void energyToMass(double amount) {
    life.energyToMass(amount);
  }  
  
  /**
   * Determines whether this critter is gone and can be removed from the world
   */
  public boolean isGone() {
    return life.isGone();
  }
    
  /**
   * Short version of the ID
   */
  public String getShortId() {
    return life.getShortId();
  }
  
  /**
   * Move to a new location
   */
  public void moveTo(int inX, int inY) {
    int x = inX;
    int y = inY;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (x> Const.MAX_X) x = Const.MAX_X;
    if (y> Const.MAX_Y) y = Const.MAX_Y;
    life.setXY(x, y);
  }

  /**
   * Number of babies in the womb
   */
  public int getWombSize() {
    return life.getWomb().size();
  }
  
  /**
   * Returns the baby at the specified position in the womb
   */
  public LifeI getBaby(int babyIndex) {
    return new LifeI(life.getWomb().get(babyIndex));
  }
  
  /**
   * Adds a fetus to your womb
   */
  public void conceiveLife(double massToFetus, double spawnDistance) {
    Life baby = new Life(life.getName(), massToFetus);
    baby.setSpawnDistance(spawnDistance);
    life.getWomb().add(baby);
    //  Index is the last position (always added at the end of the list)
    addMassToFetus((life.getWomb().size()-1), massToFetus);
  }
 
  /**
   * Takes your mass and adds it to the baby
   * @param babyIndex is the index of the baby in your womb
   */
  public void addMassToFetus(int babyIndex, double mass) {
    Life baby = life.getWomb().get(babyIndex);
    
    // Reduce the parent (distance costs more to produce)
    life.addMass(-mass * baby.getSpawnDistance() * Const.SPAWN_DISTANCE_MASS_COST);
    // Increase the baby
    baby.addMass(mass * Const.MASS_TO_FETUS_MASS);
  }
  
  /**
   * Adds a baby to the world
   */
  public void deliverBaby(int babyIndex) {
    Life baby = life.getWomb().get(babyIndex);
    double spawnDistance = baby.getSpawnDistance();
    
    // Some variation in exactly how far away 
    double distance = spawnDistance + (random.nextDouble()*(spawnDistance / 2)) - (spawnDistance/4);
    
    // Give birth spawnDistance away at a random angle (in radians)
    double angle = random.nextDouble() * (Math.PI / 2);
    double spawnX = Math.sin(angle) * distance;
    double spawnY = Math.cos(angle) * distance;
    if (random.nextBoolean()) spawnX = -spawnX;
    if (random.nextBoolean()) spawnY = -spawnY;
    
    // offset to the parent
    spawnX += life.getX();
    spawnY += life.getY();
    baby.setXY((int)spawnX,(int)spawnY);
    baby.inheritFrom(life); // pass on the genes
    World.getWorld().addBirth(baby); // Add to the world
    life.getWomb().remove(babyIndex); // remove the baby from the womb    
  }
  
  
  /**
   * Returns a list of all the neighbors overlapping you
   */
  public List<LifeI> getNeighbors() {
    if (neighbors == null ) {
      neighbors = World.getWorld().getNeighbors(life);
    }
    List<LifeI> neighborsI = new ArrayList<LifeI>();
    for (Life neighbor : neighbors.getNeighbors()) {
      neighborsI.add(new LifeI(neighbor));
    }
    return neighborsI;
  }

  
  /**
  * Try to eat someone
  */
  public void attack(int neighborIndex) {
    if (neighbors == null ) {
      neighbors = World.getWorld().getNeighbors(life);
    }
    Life him = neighbors.getNeighbors().get(neighborIndex);
    double hisMass = him.getMass();
    
    // If he's a plant that's smaller than me
    if ((life.getMass() > hisMass) && him.getPhotosynthesis()) {
      try {
        him.addMass(-hisMass); // eat him all up! 
      } catch (DeathException e) {
        // Do nothing.. someone has to die if we're going to eat
      }
      life.addEnergy(hisMass * Const.HERBIVORE_INTAKE_MASS);
    }
    
  }
  
  // ------------------------------- Getter / Setters -----------------------------------------
  
  public long getId() {
    return life.getId();
  }
  
  public int getX() {
    return life.getX();
  }

  public int getY() {
    return life.getY();
  }

  public String getName() {
    return life.getName();
  }
  
  public boolean getPhotosynthesis() {
    return life.getPhotosynthesis();
  }

  public double getStomachSize() {
    return life.getStomachSize();
  }
  
  public String getAI() {
    return life.getAI();
  }

  public void setAI(String ai) {
    life.setAI(ai);
  }

  public double getEnergy() {
    return life.getEnergy();
  }

  public double getMass() {
    return life.getMass();
  }
  
  public boolean isAlive() {
    return life.isAlive();
  }

  public int getDaysDead() {
    return life.getDaysDead();
  }

  public int getDaysAlive() {
    return life.getDaysAlive();
  }

  public double getRadius() {
    return life.getRadius();
  }
  
  public double getArea() {
    return life.getArea();
  }

  public double getHeight() {
    return life.getHeight();
  }

  public String getColor() {
    return life.getColor();
  }

  public void setColor(String color) {
    life.setColor(color);
  }

  public String getImgFile() {
    return life.getImgFile();
  }
  
  public int getImgWidth() {
    return life.getImgWidth();
  }

  public void setImage(String imgFile, int imgWidth) {
    life.setImage(imgFile, imgWidth);
  }
  
  public String getImgClass() {
    return life.getImgClass();
  }

  public void setSvgClass(String imgClass) {
    life.setImgClass(imgClass);
  }
}
