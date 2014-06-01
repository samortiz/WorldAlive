package com.alwaysrejoice.worldalive;

public class Stat {

  private String name;
  private int count;
  private double max;
  private double avg;
  
  public String toString() {
    return name+" #"+count+" max="+Const.f(max)+" avg="+Const.f(avg);
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getCount() {
    return count;
  }
  public void setCount(int count) {
    this.count = count;
  }
  public double getMax() {
    return max;
  }
  public void setMax(double max) {
    this.max = max;
  }
  public double getAvg() {
    return avg;
  }
  public void setAvg(double avg) {
    this.avg = avg;
  }
}
