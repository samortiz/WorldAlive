package com.alwaysrejoice.worldalive.ai;

import com.alwaysrejoice.worldalive.Const;

public class HerbivoreAI extends BaseAI {
  
  private static final long serialVersionUID = 1L;
  double dir = random.nextDouble() * (Math.PI * 2);
  
  public HerbivoreAI() {
    this.massToStartReproducing = 7000;
    this.litterSize = 1;
    this.spawnDistance = 1;
    this.massToFetus = 5;
    this.birthMass = 500;
    this.minMass = 490;
  }
  
  
  public void run() {
    reproduce();

    // Eat plants and move 
    graze();
    
    // In case there are no neighbors... 
    moveOn();
    
    convertExtraEnergyToMass();

    starvationCheck();
    //System.out.println(me.getShortId()+" endMass="+Const.f(me.getMass())+ " action="+Const.f(me.getAction())+ 
    //     " stomachMass "+Const.f(me.getStomachContentMass())+"/"+Const.f(me.getMaxStomachMass()));
    //System.out.println(me);
  }
  
  public void graze() {
    int maxLoops = 10;
    while (!stopGrazing() && (maxLoops > 0)) {
      maxLoops--;
      me.sortNeighborsByMass(true);
      for (int i=0; i < me.getNeighborCount(true); i++) {
        LifeI neighbor = me.getNeighbor(true, i);
        if (neighbor.getMass() < me.getMass()) {
          double maxStomachCapacity = me.getMaxStomachMass();
          double netEnergy = me.getNetEnergyFromEating(i, true);
          //System.out.println(me.getShortId()+" mass="+Const.f(me.getMass())+" hisMass="+Const.f(neighbor.getMass())+ 
          //    " net="+Const.f(netEnergy)+" action="+Const.f(me.getAction())+
          //    " stomach "+Const.f(me.getStomachContentMass())+"/"+Const.f(me.getMaxStomachMass()));
          if (netEnergy > 0) {
            me.attack(true, i);
          } 
   
          // If we have some food in our stomach, and the rest of the plants are small we will move on
          if ((me.getStomachContentMass() > (maxStomachCapacity * 0.1)) && 
              (netEnergy < (me.getMass() * 0.01))) {
            if (moveOn()) {
              // We need to clear the neighbors and reload them for the new position
              me.clearNeighbors(true);
              break;
            }
          }
          // Short out of the loop
          if (stopGrazing() || (netEnergy < 0)) {
            break;
          }
        }
      }// for
    } // while
  }
  
  
  /**
   * Condition to stop grazing 
   */
  public boolean stopGrazing() {
    return (me.getAction() < Const.ACTION_COST_TO_ATTACK) || (me.getSpaceInStomach() == 0);
  }
  
  /**
   * Moves in a straight line until it hits a wall, then picks a random angle 
   * @returns true if the animal moved
   */
  public boolean moveOn() {
    boolean moved = false;
    // If we are against a wall
    if ((me.getX() == 0) || (me.getY() == 0) ||
        (me.getX() == Const.MAX_X) || (me.getY() == Const.MAX_Y)) {
      dir = random.nextDouble() * (Math.PI * 2);
    }

    // Preferred distance
    double distance = me.getRadius() * 1.5;
    if (distance > me.getMaxDistance()) distance = me.getMaxDistance();
    // Move in a straight line
    if (distance > 0) {
      moved = me.moveDir(dir, distance);
    }
    return moved;
  }
  
}
