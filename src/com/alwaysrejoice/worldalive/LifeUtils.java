package com.alwaysrejoice.worldalive;

import java.text.DecimalFormat;

public class LifeUtils {

  /**
   * Set default values for variables that are missing values
   */
  public static void setDefaults(Life life) {
    // Default values 
    if (life.getId() == 0) life.setId(System.currentTimeMillis());
  }
 
  
  /**
   * Life grows by regulation
   */
  public static void validate(Life life) {
    // everyone has a womb
    if (life.getWomb() == null) {
      life.kill();
    }
    
    // Must be at least 1! (or else it's reducing the cost to produce a child)
    if (life.getSpawnDistance() < 1.0) {
      life.kill();
    }
    
    // NaN
    if (Double.isNaN(life.getArea()) || Double.isNaN(life.getRadius()) || 
        Double.isNaN(life.getMass()) || Double.isNaN(life.getHeight()) ) {
      System.out.println("Error in LifeUtils.validate! Invalid number : "+life.getArea()+"a "+life.getRadius()+"r "+life.getMass()+"g "+life.getHeight()+"h");
      life.kill();
    }
    
    // If you are too small you get removed
    if (life.getMass() < Const.MIN_MASS) {
      life.kill();
    }
    
    // Make sure you are in the world
    if ((life.getX() < 0) || (life.getY() < 0) || 
       (life.getX() > Const.MAX_X) || (life.getY() > Const.MAX_Y)) {
      life.kill();
    }
    
  }
  
  
  /** 
   * Thanks to Chris Redford for the math posted on stackoverflow
   */
  public static double overlapOfTwoCircles(double radius1, double radius2, double distance) {
    // Error Checking
    if ((radius1 == 0) || (radius2 == 0) ||   // Nothing to overlap
        (distance > (radius1 + radius2))      // Too far apart
       ) {
      return 0;
    }
    Double r = radius1;
    Double R = radius2;
    Double d = distance;
    // Ensure r is smaller than R
    if (R < r) {
      r = radius2;
      R = radius1;
    }
   // Check for a full overlap
    if (distance < (R - r)) {
      // Area of smaller circle
      return Math.PI * r * r;
    }
    
    Double part1 = r*r*Math.acos((d*d + r*r - R*R)/(2*d*r));
    Double part2 = R*R*Math.acos((d*d + R*R - r*r)/(2*d*R));
    Double part3 = 0.5*Math.sqrt((-d+r+R)*(d+r-R)*(d-r+R)*(d+r+R));
    double overlap = part1 + part2 - part3;
    
    // Ensure we don't create any errors
    if (Double.isNaN(overlap) || Double.isInfinite(overlap)) {
      System.out.println("Error calculating overlap!");
      DecimalFormat f = new DecimalFormat("0.#");
      System.out.println("overlap="+f.format(overlap)+" r="+f.format(r)+" R="+f.format(R)+" d="+f.format(d)+" part1="+f.format(part1)+" part2="+f.format(part2)+" part3="+f.format(part3));
      return 0;
    }
    return overlap;
  }

  
}
