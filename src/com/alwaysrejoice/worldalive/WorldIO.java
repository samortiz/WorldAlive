package com.alwaysrejoice.worldalive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Utility IO methods for the World
 */
public class WorldIO {
  public static final String DIR = "/home/sortiz/project/world_alive/data/";
  public static final String LIVES_FILE = "List_of_Life.dat";
  
  /**
   * Loads the world from a file
   * @param worldFileName : File containing the world data
   */
  public static void load(World world) {
    System.out.println("God created the heavens and the earth.");
    ObjectInputStream in = null;
    List<Life> plants = world.getLives(true);
    List<Life> animals = world.getLives(false);
    try {
      FileInputStream streamIn = new FileInputStream(DIR+LIVES_FILE);
      in = new ObjectInputStream(streamIn);
      @SuppressWarnings("unchecked")
      List<Life> loadedLives = (List<Life>)in.readObject();
      for (Life life : loadedLives) {
        // Check that the life is valid
        try {
          LifeUtils.validate(life);
          // Add the life to the world  
          if (life.getPhotosynthesis()) {
            plants.add(life);
          } else {
            animals.add(life);
          }
        } catch (DeathException e) {
          // It's OK. He's dead
        }
        System.out.println("Loaded "+life);
      } 
    } catch (FileNotFoundException e) {
      System.out.println("Failed to find lives file at :"+DIR+LIVES_FILE);
    } catch (Exception e) {
      System.out.println("Error loading world");
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          System.out.println("Error closing input stream");
          e.printStackTrace();
        }
      } 
    }
  }
  
  
  /** 
   * Writes out the world to a file
   * @param worldFileName
   */
  public static void store(World world) {
    ObjectOutputStream out = null;
    FileOutputStream fout = null;
    List<Life> lives = world.getAllLives();
    try {
      fout = new FileOutputStream(DIR+LIVES_FILE, false);
      out = new ObjectOutputStream(fout);
      out.writeObject(lives);
     
    } catch (Exception e) {
      System.out.println("Error storing world :"+e.getMessage());
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          System.out.println("Error closing output stream :"+e.getMessage());
          e.printStackTrace();
        }
      } 
    }    
  }

  
}
