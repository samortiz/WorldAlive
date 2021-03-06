package com.alwaysrejoice.worldalive;

import java.util.List;
import java.util.Random;

public class Time implements Runnable {

  private World world = null;
  private boolean running = false;
  private boolean stopped = true;
  private int moveCount = 0;
  private StatKeeper stats = new StatKeeper();
  protected Random random = new Random();

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
    long startTime = System.currentTimeMillis();
    
    while (running == true) {
      moveCount = 0;
      startTime = System.currentTimeMillis();
      stats.reset();

      // Plants
      processLives(move, true);
      
      // Animals
      processLives(move, false);
      
      // Give birth to all the babies to be born in this round 
      // This can't be done inside the getLives loop because 
      // that would modify the list while iterating it
      for (Life baby : world.getBirths()) {
        world.addLife(baby);
        //System.out.println("Birth : "+baby);
      }
      long loopTime = System.currentTimeMillis()-startTime;
      
      // Display
      System.out.print(stats);
      System.out.println("Moves="+moveCount+" births="+world.getBirths().size()+" time="+loopTime+"\n");
      world.getBirths().clear();
      
      // Delay each loop by at least Const.CLOCK_DELAY
      try {
        long delay = Const.CLOCK_DELAY - loopTime;
        if (delay > 0) {
          Thread.sleep(delay);
        }
      } catch (InterruptedException e) {
        running = false;
      }
     
    }
    stopped = true;
    System.out.println("Time has stopped");
  }

  
  private void processLives(Move move, boolean photosynthesis) {
    // Go through all the lives
    List<Life> lives = world.getLives(photosynthesis);
    for (Life life : lives) {
      if (life.isGone()) continue;
      
      // Population cap NOTE! cannot be lives.size needs to be moves
      if ((photosynthesis) && (lives.size() > Const.MAX_LIVES)) {
        System.out.println("Lives.size ="+lives.size());
        if (random.nextDouble() > ((double)Const.MAX_LIVES / (double)lives.size())) {
          try {
            System.out.println("Killed");
            life.kill();
          } catch (Exception e) {
            // We know he's going to die
          }
        }
      }
      
      if (life.isAlive()) {
        move.move(life);
        stats.setStat(life);
        moveCount++;
      } else {
        move.decay(life);
      }
    } // for life
  }
  
}
