package com.alwaysrejoice.worldalive;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;

public class Move {

  // Move variables 
  private Random random = new Random();
  private World world;

  
  /**
   * Constructor
   */
  public Move(World world) {
    this.world = world;
  }
  
  /**
   * What the Life does on its turn
   */
  public void move(Life life) {
    try {
      // Generate photosynthesis energy
      generateEnergy(life);
      
      // Basal Metabolic Rate
      expendBMREnergy(life);
  
      // Reproduction
      reproduce(life);
      
      // Convert extra energy into mass
      if (life.getEnergy() > 0) {
        life.energyToMass(life.getEnergy() * 0.8);
      }
      
      // Ensure nothing funny is going on
      LifeUtils.validate(life);
      
      life.setDaysAlive(life.getDaysAlive()+1);
      //System.out.println(life);  
    } catch (DeathException e) {
      //System.out.println(e.getMessage());
    }
  }
  
  
  /**
   * A dead creature doesn't move, so much as decay
   */
  public void decay(Life life) {
    if (!life.isAlive()) {
      // Start decaying
      if (life.getDaysDead() > Const.DAYS_UNTIL_DECAY_STARTS) {
        life.setMass(life.getMass() * Const.DECAY_RATE);
      }
      life.setDaysDead(life.getDaysDead()+1);
    }
  }
  
  /**
   * Use energy just to stay alive
   */
  public void expendBMREnergy(Life life) {
    double bmr = life.getMass() * Const.PLANT_BMR_PER_MASS;
    life.addEnergy(-bmr);
  }
  
  /**
   * Photosynthesis energy
   */
  public void generateEnergy(Life life) {
    // Make sure this is a plant
    if (!life.getPhotosynthesis()) {
      return;
    }
    double area = life.getArea(); 
    double overlap = 0.0;
    
    Neighbors neighbors = world.getNeighbors(life);
    for (Life him : neighbors.getNeighbors()) {
      // The taller one gets the sunlight
      if (life.getHeight() < him.getHeight()) {
        double thisOverlap = LifeUtils.overlapOfTwoCircles(life.getRadius(), him.getRadius(), neighbors.getDistance(him));
        overlap += thisOverlap;
        // note : this will not take into account whether the shade is in the same place as others
      }
    } // for
    
    double newEnergy = 0.0;
    
    // Full sunlight
    if (overlap == 0.0) {
      newEnergy = area * Const.PHOTO_ENERGY_GENERATED_PER_AREA * Const.PHOTO_ENERGY_ABSORBED;
    // Some shade
    } else {
      double percentCovered = overlap / area;
      // Less than full coverage, some part is exposed to direct sunlight
      if (percentCovered < 1.0) {
        newEnergy = (area * (1-Const.PHOTO_ENERGY_ABSORBED) * percentCovered) + // shaded area
                    (area * Const.PHOTO_ENERGY_ABSORBED * (1-percentCovered)); // sunlit area
      // If the plant is fully in the shade  
      } else {
        // This assumes all shade is evenly distributed 
        newEnergy = area * Const.PHOTO_ENERGY_ABSORBED * Math.pow((1-Const.PHOTO_ENERGY_ABSORBED), percentCovered);
      }
    }
//    DecimalFormat f = new DecimalFormat("0.##");
//    System.out.println(" area="+f.format(area)+" overlap="+f.format(overlap)+" energy="+f.format(life.getEnergy())+
//        " newEnergy="+f.format(newEnergy)+" BMR="+f.format(life.getMass() * Const.PLANT_BMR_PER_MASS) + 
//        " netEnergy="+f.format(newEnergy - (life.getMass() * Const.PLANT_BMR_PER_MASS)) +
//        " mass="+f.format(life.getMass()) );
    
    // Energy generated 
    life.addEnergy(newEnergy);
  }
  
  /**
   * Handle reproduction 
   */
  public void reproduce(Life life) {
    double spawnDistance = life.getSpawnDistance();
    
    // Fetus growth
    if (life.getWomb().size() > 0) {
      Iterator<Life> babies = life.getWomb().iterator();
      while (babies.hasNext()) {
        Life child = babies.next();
        double fetusMassCost = life.getMassToFetus();
        
        // Extra costs for fetus development
        if (spawnDistance > 1) {
          fetusMassCost = fetusMassCost * spawnDistance * Const.SPAWN_DISTANCE_MASS_COST;
        }
        
        // Increase the fetus, decrease the mother
        life.addMass(-fetusMassCost);
        child.addMass(life.getMassToFetus() * Const.MASS_TO_FETUS_MASS);

        if (child.getMass() > life.getBirthMass()) {
          // Some variation in exactly how far away 
          spawnDistance = spawnDistance + (random.nextDouble()*(spawnDistance / 2)) - (spawnDistance/4);
          
          // Give birth spawnDistance away at a random angle (in radians)
          double angle = random.nextDouble() * (Math.PI / 2);
          double spawnX = Math.sin(angle) * spawnDistance;
          double spawnY = Math.cos(angle) * spawnDistance;
          if (random.nextBoolean()) spawnX = -spawnX;
          if (random.nextBoolean()) spawnY = -spawnY;
          
          // offset to the parent
          spawnX += life.getX();
          spawnY += life.getY();
          child.moveTo((int)spawnX,(int)spawnY);
          child.inheritFrom(life); // pass on the genes
          world.addBirth(child); // Add to the world
          babies.remove(); // remove from the womb
        }
        
      }
    }

    
    // Conception
    if (life.getMass() > life.getMassToStartReproducing()) {
      // If womb is empty conceive (all at once)
      if (life.getWomb().size() == 0) {
        for (int i=0; i<life.getLitterSize(); i++) {
          life.addMass(-life.getMassToFetus());
          Life child = new Life(life.getName(), life.getMassToFetus());
          life.getWomb().add(child);
        } // for
      }
    }
  }
  
  
}
