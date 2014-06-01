package com.alwaysrejoice.worldalive;

import java.text.DecimalFormat;

public class Const {

  // -------------------- System -----------------------------------

  public static final int CLOCK_DELAY = 500; // each loop should be at least this many milliseconds
  public static final DecimalFormat f = new DecimalFormat("0");
  
  
  // ----------------- Conversion ----------------------------------
  
  public static final double ENERGY_TO_MASS = 0.7; // putting on fat (feasting)
  public static final double MASS_TO_ENERGY = 0.7;  // burning fat (starving)
  public static final double MASS_TO_FETUS_MASS = 0.9; // mass used to grow a baby

  
  // ----------------------- Limits---------------------------------
  
  // If you are smaller than this you are gone, and your place can be taken by someone else
  public static final double MIN_MASS = 10;

  // Size of the world
  public static final int MAX_X = 300;
  public static final int MAX_Y = 200;
  
  
  // ---------------------- Reproduction ---------------------------
  
  // How much mass it costs per turn to grow a fetus that can be born spawnDistance away
  public static final double SPAWN_DISTANCE_MASS_COST = 1.0;
  
  
  // ---------------- Photosynthesis -------------------------------
  
  // What percent of the energy is absorbed, determining how much is left for others under you
  // For example if set to 0.8  then 80% of the light is absorbed and plants under one layer 
  // will only receive 20% of the PHOTO_ENERGY_GENERATED_PER_AREA 
  // This should be (0 < x <= 1)
  public static final double PHOTO_ENERGY_ABSORBED = 0.6; 
  public static final double PHOTO_ENERGY_GENERATED_PER_AREA = 1.0;
  public static final double PLANT_BMR_PER_MASS = 0.04; // energy burned 

  
  // ------------------ Animals ---------------------------------------
  
  public static final double BASE_ANIMAL_BMR_PER_MASS = 0.1; // energy burned 
  public static final double HERBIVORE_INTAKE_MASS = 0.5;  // energy a herbivore gets from a plant
  public static final double CARNIVORE_INTAKE_MASS = 0.99; // energy a carnivore gets from an animal
  public static final double ATTACK_COST = 0.005; // cost in energy to attack scaled by your mass
 
  
  // --------------------- Action -------------------------------------
  
  public static final double ACTION_SCALE = 300; // How much extra action you get for your metabolism
  public static final double ACTION_COST_TO_ATTACK = 10; // how much action it costs to attack 
  public static final double ACTION_COST_TO_MOVE_SCALE = 100; // how much action it costs to move 2 * radius
  public static final double ACTION_COST_TO_CONCEIVE = 50; 
  public static final double ACTION_COST_TO_DELIVER_BABY = 100;
  
  
  // -------------------- Death and Decay --------------------------
  // How long until decay starts
  public static final int DAYS_UNTIL_DECAY_STARTS = 3;
  // Percent that decays with each passing day
  public static final double DECAY_RATE = 0.5;
  
  
  /**
   * Convenience method to format 
   */
  public static String f (double toFormat) {
    return f.format(toFormat);
  }
  
}
