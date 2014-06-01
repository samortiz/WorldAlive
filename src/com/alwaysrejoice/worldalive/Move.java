package com.alwaysrejoice.worldalive;

import java.io.Serializable;

import com.alwaysrejoice.worldalive.ai.BaseAI;

public class Move implements Serializable {

  private static final long serialVersionUID = 1L;
  // Move variables 
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
  
      // Run the life's AI
      runAI(life);
      
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
    double bmr = 0;
    if (life.getPhotosynthesis()) {
      bmr = life.getMass() * Const.PLANT_BMR_PER_MASS;
    } else {
      // Animals can lower their BMR by not using all their action points
      bmr = (life.getMass() * Const.BASE_ANIMAL_BMR_PER_MASS) * life.getTotalAction();
      if (bmr == 0) bmr = 10; // Nobody lives for free 
    }
    
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
    
    Neighbors neighbors = world.getNeighbors(life, true); // only plants
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
   * Runs the AI for a life
   */
  public void runAI(Life life) {
    BaseAI aiInstance = life.getAiInstance();

    // If we don't have an instance created yet
    if (aiInstance == null) {
      String aiClass = life.getAIClass();
      // If there is no AI, just exit
      if (aiClass == null) return;
      
      try {
        Object someAI = Class.forName("com.alwaysrejoice.worldalive.ai."+aiClass).newInstance();
        if (someAI instanceof BaseAI) {
          aiInstance = (BaseAI) someAI;
          aiInstance.setLife(life);
          life.setAiInstance(aiInstance);
        }
             
      } catch (Exception e) {
        System.out.println("Error creating instance of AI '"+aiClass+"' for : "+life.getName()+" id="+life.getId());
        e.printStackTrace();
      }
    }
    
    if (aiInstance != null) {
      life.setAction(calculateAction(life)); // full action to start
      life.setStomachContentMass(0); // empty stomach to start
      aiInstance.run();
      aiInstance.clearNeighbors();
    }
  }

  /**
   * Determines how much movement this creature can do in one turn
   */
  private double calculateAction(Life life) {
    return 100 + (life.getMetabolism() * Const.ACTION_SCALE);
  }
  
}
