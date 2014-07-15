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

    Life l;
    //world.getLives(true).clear();
    //world.getLives(false).clear();
    
    l = new Life("Grass", 100, 100, 10);
    l.setPhotosynthesis(true);
    l.setImage("grass.svg", 100);
    l.setAIClass("GrassAI");
    world.addBirth(l);

    l = new Life("Grass", 150, 100, 10);
    l.setPhotosynthesis(true);
    l.setImage("grass.svg", 100);
    l.setAIClass("GrassAI");
    world.addBirth(l);

    l = new Life("Tree", 250, 100, 4000);
    l.setPhotosynthesis(true);
    l.setImage("tree.svg", 100);
    l.setAIClass("TreeAI");
    //world.addBirth(l);

    l = new Life("Cow", 80, 100, 1000);
    l.setPhotosynthesis(false);
    l.setAttack(0);
    l.setDefence(0.1);
    l.setStomachSize(0.2);
    l.setMetabolism(0);
    l.setImage("cow.svg", 100);
    l.setAIClass("HerbivoreAI");
    world.addBirth(l);

    l = new Life("Elk", 80, 100, 400);
    l.setPhotosynthesis(false);
    l.setAttack(0);
    l.setDefence(0.2);
    l.setStomachSize(0.8);
    l.setMetabolism(0);
    l.setImage("elk.svg", 100);
    l.setAIClass("ElkAI");
    world.addBirth(l);

    l = new Life("Predator", 80, 100, 1000);
    l.setPhotosynthesis(false);
    l.setAttack(0.3);
    l.setDefence(0.2);
    l.setStomachSize(0.5);
    l.setMetabolism(0);
    l.setImage("spikey.svg", 100);
    l.setAIClass("CarnivoreAI");
    //world.addBirth(l);
    
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    time.stop();
    WorldIO.store(world);
    System.out.println("It's the end of the world...\n\n");
  }
 
}