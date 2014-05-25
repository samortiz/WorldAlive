package com.alwaysrejoice.worldalive;

import java.text.DecimalFormat;

public class Time implements Runnable {

  private World world = null;
  private boolean running = false;
  private boolean stopped = true;
  
  
  /**
   * Construct a new time with the world
   */
  public Time(World world) {
    this.world = world;
  }
  
  /**
   * Stops time and exits the Thread
   */
  public void stop() {
    stopped = false;
    running = false;
    while (!stopped) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
    }
  }
  
  
  /**
   * Start time (call Time.start() to begin time)
   */
  @Override
  public void run() {
    running = true;
    System.out.println("Time is ticking");
    Move move = new Move(world);
    int moveCount = 0;
    long startTime = System.currentTimeMillis();
    double maxMass = 0;
    DecimalFormat f = new DecimalFormat("0.##");
    
    while (running == true) {
      moveCount = 0;
      startTime = System.currentTimeMillis();
      maxMass = 0;
      
      // Go through all the lives
      for (Life life : world.getLives()) {
        if (life.isGone()) continue;
        
        if (life.isAlive()) {
          move.move(life);
          moveCount++;
          if (life.getMass() > maxMass) maxMass = life.getMass();
        } else {
          move.decay(life);
        }
      } // for life
      
      // Give birth to all the babies to be born in this round 
      // This can't be done inside the getLives loop because 
      // that would modify the list while iterating it
      for (Life baby : world.getBirths()) {
        world.addLife(baby);
        //System.out.println("Birth : "+baby);
      }
      System.out.println("Moves="+moveCount+" maxMass="+f.format(maxMass)+" births="+world.getBirths().size()+
          " time="+(System.currentTimeMillis()-startTime));
      world.getBirths().clear();
      
      //System.out.println("--------------- "+moveCount+" -----------------");
      
      try {
        Thread.sleep(Const.CLOCK_DELAY);
      } catch (InterruptedException e) {
        running = false;
      }
     
    }
    stopped = true;
    System.out.println("Time has stopped");
  }

  
}
