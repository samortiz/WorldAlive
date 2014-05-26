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

    world.getLives().clear();
   
    Life l = new Life("Shrub", 25, 25, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(6000);
    l.setLitterSize(1);
    l.setSpawnDistance(30);
    l.setMassToFetus(Const.MIN_MASS+0.1);
    l.setBirthMass(Const.MIN_MASS);
    l.setColor("#40DD40");
    world.addBirth(l);
 
    l = new Life("Green", 75, 25, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(Const.MIN_MASS*12*2 + 500);
    l.setLitterSize(2);
    l.setSpawnDistance(12);
    l.setMassToFetus(Const.MIN_MASS+0.1);
    l.setBirthMass(Const.MIN_MASS);
    //l.setSvgImage("grass_16x16.svg", 16);
    l.setColor("#00EE99");
    world.addBirth(l);
 
    l = new Life("Blue", 75, 75, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(6000);
    l.setLitterSize(5);
    l.setSpawnDistance(25);
    l.setMassToFetus(10);
    l.setBirthMass(Const.MIN_MASS+1);
    l.setColor("#009977");
    world.addBirth(l);

    l = new Life("Yellow", 25, 75, 10);
    l.setPhotosynthesis(true);
    l.setMassToStartReproducing(3000);
    l.setLitterSize(3);
    l.setSpawnDistance(14);
    l.setMassToFetus(10);
    l.setBirthMass(Const.MIN_MASS+1);
    l.setColor("#224422");
    world.addBirth(l);
 
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    time.stop();
    WorldIO.store(world);
    System.out.println("It's the end of the world...\n\n");
  }
 
}