package com.alwaysrejoice.worldalive;

import com.alwaysrejoice.worldalive.ai.BaseAI;

/**
 * Handles the artificial intelligence for creatures
 */
public class Ai {
  
  public static void run(Life life) {
    String ai = life.getAI();

    // If there is no AI, just exit
    if (ai == null) return;
    
    try {
      Object someAI = Class.forName("com.alwaysrejoice.worldalive.ai."+ai).newInstance();
      if (someAI instanceof BaseAI) {
        BaseAI lifeAI = (BaseAI) someAI;
        lifeAI.setLife(life);
        lifeAI.run();
      }
           
    } catch (Exception e) {
      System.out.println("Error running AI '"+ai+"' for : "+life.getName()+" id="+life.getId());
      e.printStackTrace();
    }
  }

}
