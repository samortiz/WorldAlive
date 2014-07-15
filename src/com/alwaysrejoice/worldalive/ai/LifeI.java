package com.alwaysrejoice.worldalive.ai;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

import com.alwaysrejoice.worldalive.Const;
import com.alwaysrejoice.worldalive.DeathException;
import com.alwaysrejoice.worldalive.Life;
import com.alwaysrejoice.worldalive.Neighbors;
import com.alwaysrejoice.worldalive.World;

public class LifeI implements Serializable {

  private static final long serialVersionUID = 1L;
  
  // Current life's instance
  private Life life = null;
  protected Random random = new Random();
  private transient Neighbors plantNeighbors = null;
  private transient Neighbors animalNeighbors = null;

  // ------------------- Comparators --------------------------
  public static Comparator<Life> massComparatorDesc = new Comparator<Life>() {
    public int compare(Life a, Life b) {
      return (int)(b.getMass() - a.getMass());
    }
  };
  
  
  
  
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
   * Kill yourself (population control)
   */
  public void suicide() {
    life.kill();
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
  private void moveTo(int inX, int inY) {
    int x = inX;
    int y = inY;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (x> Const.MAX_X) x = Const.MAX_X;
    if (y> Const.MAX_Y) y = Const.MAX_Y;
    life.setXY(x, y);
  }
  
  /**
   * Moves in the direction specified, the distance specified
   * This will cost energy and actionTime
   */
  public boolean moveDir(double angle, double distance) {
    double actionCost = getActionCostToMove(distance);
    if (actionCost > life.getAction()) {
      return false;
    }
    life.addAction(-actionCost);
    double x= life.getX() + Math.sin(angle) * distance;
    double y= life.getY() + Math.cos(angle) * distance;
    moveTo((int)x,(int)y);
    //System.out.println(life.getShortId()+" moved "+Const.f(distance)+" actionCost="+Const.f(actionCost)+
    //    " actionLeft="+Const.f(life.getAction()));
    return true;
  }

  /**
   * Given your action left, how far can you move
   */
  public double getMaxDistance() {
    return (life.getAction() * 2 * life.getRadius()) / Const.ACTION_COST_TO_MOVE_SCALE;
  }
  
  public double getActionCostToMove(double distance) {
    // distance / 2r results in 100 action to move double your radius (a completely new spot)
    return (distance / (life.getRadius() * 2)) * Const.ACTION_COST_TO_MOVE_SCALE;
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
    if (life.getAction() >= Const.ACTION_COST_TO_CONCEIVE) {
      Life baby = new Life(life.getName(), massToFetus);
      baby.setSpawnDistance(spawnDistance);
      life.getWomb().add(baby);
      //  Index is the last position (always added at the end of the list)
      addMassToFetus((life.getWomb().size()-1), massToFetus);
      life.addAction(-Const.ACTION_COST_TO_CONCEIVE);
    }
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
    if (life.getAction() >= Const.ACTION_COST_TO_DELIVER_BABY) {
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
      World.getWorld().addBirth(baby); // Add baby to the world
      life.getWomb().remove(babyIndex); // remove the baby from the womb    
      life.addAction(-Const.ACTION_COST_TO_DELIVER_BABY);
    }
  }

  /** 
   * Resets the neighbors
   */
  public void clearNeighbors(boolean photosynthesis) {
    if (photosynthesis) {
      plantNeighbors = null;
    } else {
      animalNeighbors = null;
    }
  }
  
  /**
   * Initialize neighbors if it's null
   */
  private Neighbors getNeighbors(boolean photosynthesis) {
    if (photosynthesis) {
      if (plantNeighbors == null ) {
        plantNeighbors = World.getWorld().getNeighbors(life, true);
      }
      return plantNeighbors;
    } else {
      if (animalNeighbors == null ) {
        animalNeighbors = World.getWorld().getNeighbors(life, false);
      }
      return animalNeighbors;
    }
  }
  
  public int getNeighborCount(boolean photosynthesis) {
    return getNeighbors(photosynthesis).getNeighborCount();
  }

  /**
   * Returns a neighbor at the specified index 
   */
  public LifeI getNeighbor(boolean photosynthesis, int neighborIndex) {
    return new LifeI(getNeighbors(photosynthesis).get(neighborIndex));
  }

  /**
   * Returns the bare life object
   */
  private Life getNeighborLife(boolean photosynthesis, int neighborIndex) {
    return getNeighbors(photosynthesis).get(neighborIndex);
  }
  
  
  /**
   * Sorts the list of neighbors by mass
   */
  public void sortNeighborsByMassDesc(boolean photosynthesis) {
    Collections.sort(getNeighbors(photosynthesis).getNeighbors(), massComparatorDesc);
  }
  
  
  public double costToAttack(int neighborIndex) {
    return Const.ATTACK_COST * life.getMass();
  }
 
  /** 
   * Calculates how much energy you would get from eating this neighbor
   */
  public double getEnergyFromEating(boolean photosynthesis, int neighborIndex) {
    Life him = getNeighborLife(photosynthesis, neighborIndex);
    // Plant
    if (him.getPhotosynthesis()) {
      return him.getMass() * Const.HERBIVORE_INTAKE_MASS;
    } 
    // Animal
    return him.getMass() * Const.CARNIVORE_INTAKE_MASS;
  }
  
  public double getNetEnergyFromEating(boolean photosynthesis, int neighborIndex) {
    return getEnergyFromEating(photosynthesis, neighborIndex) - costToAttack(neighborIndex);
  }
  
  /** 
   * Maximum amount of mass that fits in your stomach (set from stomachSize * mass)
   */
  public double getMaxStomachMass() {
    return life.getStomachSize() * life.getMass();
  }
  
  /**
   * How much space you have left
   */
  public double getSpaceInStomach() {
    return getMaxStomachMass() - life.getStomachContentMass();
  }
  
  
  /**
  * Try to eat someone
  */
  public void attack(boolean photosynthesis, int neighborIndex) {
    // Action cost to attack
    if (life.getAction() < Const.ACTION_COST_TO_ATTACK) {
      return;
    }
    life.addAction(-Const.ACTION_COST_TO_ATTACK);
    
    Life him = getNeighborLife(photosynthesis, neighborIndex);
    double hisMass = him.getMass();
    
    double energyCost = costToAttack(neighborIndex);
    life.addEnergy(-energyCost);
    
    // If he's a plant then the rules are a little different 
    if (him.getPhotosynthesis()) {
      double spaceInStomach = getSpaceInStomach();
      
      // You can only eat plants that are shorter than you
      if ((him.getHeight() < life.getHeight()) && (spaceInStomach > 0)) {
        try {
          him.addMass(-hisMass); // eat him  
        } catch (DeathException e) {
          // Do nothing.. someone has to die if we're going to eat
        }
        double massEaten = hisMass;
        double newStomachContents = life.getStomachContentMass() + hisMass;
        if (spaceInStomach < hisMass) {
          // You can only eat part of him... 
          massEaten = spaceInStomach;
          newStomachContents = getMaxStomachMass();
        }
        life.addEnergy(massEaten * Const.HERBIVORE_INTAKE_MASS);
        life.setStomachContentMass(newStomachContents);
       
       //System.out.println(life.getShortId()+" mass="+Const.f(life.getMass())+") ate "+Const.f(hisMass) +
       //    " massEaten="+Const.f(massEaten)+" stomachContents="+Const.f(life.getStomachContentMass())+" maxStomach="+Const.f(maxStomachMass()));
      }
    
    // Attacking an animal
    } else { 
      double attackFactor = (life.getAttack() * life.getMass()) - (him.getDefence() * him.getMass());
      if (attackFactor > 0) {
        him.addMass(-hisMass);
        life.addEnergy(hisMass * Const.CARNIVORE_INTAKE_MASS);
      } else {
        // Whoops he was too strong for you to kill
        life.addEnergy(-hisMass * 0.3); // ouch that hurt! 
      }
        
    }
    
    
    
  }
  
  // ------------------------------- Getter / Setters -----------------------------------------
  
  public UUID getId() {
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
  
  public String getAIClass() {
    return life.getAIClass();
  }

  public void setAIClass(String aiClass) {
    life.setAIClass(aiClass);
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
  
  public double getAction() {
    return life.getAction();
  }
  
  public double getStomachContentMass() {
    return life.getStomachContentMass();
  }
  
}
