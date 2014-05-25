package com.alwaysrejoice.worldalive;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
 
public class WorldServletListener implements ServletContextListener {

  private World world = null;
  private Thread timeThread = null;
  private Time time = null;
  
  @Override
  public void contextInitialized(ServletContextEvent arg0) {
    System.out.println("In the beginning"); 
    world = World.getWorld();
 
    // Start time
    time = new Time(world);
    timeThread = new Thread(time);
    timeThread.start();

    
    //world.getLives().clear();
    Life l = new Life("Purple", 25, 25, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(200000000);
    l.setColor("purple");
    world.addBirth(l);
 
    l = new Life("Green", 75, 25, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(100);
    l.setLitterSize(1);
    l.setSpawnDistance(30);
    l.setMassToFetus(Const.MIN_MASS+0.1);
    l.setBirthMass(Const.MIN_MASS);
    l.setColor("green");
    world.addBirth(l);
 
    l = new Life("Blue", 75, 75, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(10000);
    l.setLitterSize(1);
    l.setSpawnDistance(40);
    l.setMassToFetus(10);
    l.setBirthMass(120);
    l.setColor("blue");
    world.addBirth(l);

    l = new Life("Yellow", 25, 75, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(1000);
    l.setLitterSize(2);
    l.setSpawnDistance(30);
    l.setMassToFetus(Const.MIN_MASS+0.1);
    l.setBirthMass(Const.MIN_MASS*3);
    l.setColor("yellow");
    world.addBirth(l);
    
    
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    time.stop();
    WorldIO.store(world);
    System.out.println("It's the end of the world...\n\n");
  }
 
}