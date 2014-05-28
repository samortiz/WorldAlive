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
 
    Life l = new Life("Grass", 100, 100, 10);
    l.setPhotosynthesis(true);
    l.setImage("grass.svg", 100);
    l.setAI("GrassAI");
    world.addBirth(l);

    l = new Life("Grass", 150, 100, 10);
    l.setPhotosynthesis(true);
    l.setImage("grass.svg", 100);
    l.setAI("GrassAI");
    world.addBirth(l);
    
    
    l = new Life("Tree", 250, 100, 10);
    l.setPhotosynthesis(true);
    l.setImage("spore.svg", 100);
    l.setAI("TreeAI");
    world.addBirth(l);

    l = new Life("Herbivore", 80, 100, 6000);
    l.setPhotosynthesis(false);
    l.setColor("brown");
    l.setAI("HerbivoreAI");
    world.addBirth(l);
    
    
 
  }

  @Override
  public void contextDestroyed(ServletContextEvent arg0) {
    time.stop();
    WorldIO.store(world);
    System.out.println("It's the end of the world...\n\n");
  }
 
}