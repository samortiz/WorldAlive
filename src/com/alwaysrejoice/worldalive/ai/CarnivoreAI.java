package com.alwaysrejoice.worldalive.ai;

public class CarnivoreAI extends BaseAI {

  private static final long serialVersionUID = 1L;

  public CarnivoreAI() {
    this.massToStartReproducing = 10000;
    this.litterSize = 1;
    this.spawnDistance = 1;
    this.massToFetus = 5;
    this.birthMass = 1000;
    this.minMass = 500;
  }

  public void run() {
    reproduce();
    hunt();
    convertExtraEnergyToMass();
    starvationCheck();
  }
  

  public void hunt() {
    me.sortNeighborsByMassDesc(false);
    for (int i=0; i < me.getNeighborCount(false); i++) {
      if ((me.getSpaceInStomach() > 0) && (me.getNetEnergyFromEating(false, i) > 0)) {
        me.attack(false,  i);
      }
    }// for
   
  }
  
}
