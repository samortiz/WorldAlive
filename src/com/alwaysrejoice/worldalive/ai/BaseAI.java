package com.alwaysrejoice.worldalive.ai;

import java.io.Serializable;
import java.util.Random;

import com.alwaysrejoice.worldalive.Const;
import com.alwaysrejoice.worldalive.Life;

public class BaseAI implements Serializable {

  private static final long serialVersionUID = 1L;
  protected Random random = new Random();
  protected LifeI me = null;
  
  // Reproduction
  protected double spawnDistance = 2; // distance offspring are born
  protected double birthMass = Const.MIN_MASS; // size of baby when born
  protected double massToFetus = 1; // mass to each fetus each turn
  protected double litterSize = 1; // Max number of fetus in womb at one time
  protected double massToStartReproducing = Const.MIN_MASS * spawnDistance * litterSize; // when to start reproducing
  protected double minMass = Const.MIN_MASS; // if your mass falls below this you die (useful for limiting population growth)
  
  public void setLife(Life life) {
    this.me = new LifeI(life);
  }
  
  public LifeI getLifeI() {
    return me;
  }
  
  /**
   * Called at the end of your turn
   */
  public void clearNeighbors() {
    me.clearNeighbors(true); // plants
    me.clearNeighbors(false); // animals
  }
  
  /**
   * Base class AI 
   */
  public void run() {
    reproduce();
    convertExtraEnergyToMass();
    starvationCheck();
  }
  
  /**
   * Handles reproduction 
   */
  public void reproduce() {
    fetusGrowth();
    conceive();
  }
  
  /**
   * Taking care of the next generation
   */
  public void fetusGrowth() {
    int babyIndex = me.getWombSize() -1;
    while (babyIndex >= 0) {
      LifeI baby = me.getBaby(babyIndex);
      me.addMassToFetus(babyIndex, massToFetus);
      if (baby.getMass() >= birthMass) {
        // Note: This will change the wombSize (so we need to count down so it doesn't mess up the loop)
        me.deliverBaby(babyIndex);
      }
      babyIndex--;
    } // while
  }

  /**
   * Conceive new babies
   */
  public void conceive() {
    // Conception
    if (me.getMass() > massToStartReproducing) {
      // If womb is empty conceive (all at once)
      if (me.getWombSize() == 0) {
        for (int i=0; i<litterSize; i++) {
          me.conceiveLife(massToFetus, spawnDistance);
        } // for
      }
    }
  }
  
  /**
   * Converts energy to mass
   */
  public void convertExtraEnergyToMass() {
    // Convert extra energy into mass
    if (me.getEnergy() > 0) {
      me.energyToMass(me.getEnergy() * 0.8);
    }
  }
  
  /**
   * If you are greatly reduced die.
   * This will control the population level 
   */
  public void starvationCheck() {
    if (me.getMass() < minMass) {
      me.suicide();
    }
  }
  
}
