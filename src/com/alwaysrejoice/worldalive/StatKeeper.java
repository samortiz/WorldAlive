package com.alwaysrejoice.worldalive;

import java.util.HashMap;

public class StatKeeper {

  private HashMap<String, Stat> stats = new HashMap<String, Stat>();
  
  public void setStat(Life life) {
    Stat stat = stats.get(life.getName());
    if (stat == null) {
      stat = new Stat();
      stat.setName(life.getName());
      stat.setCount(1);
      stat.setAvg(life.getMass());
      stat.setMax(life.getMass());
      stats.put(life.getName(), stat);
    } else {
      stat.setCount(stat.getCount()+1);
      if (stat.getMax() < life.getMass()) {
        stat.setMax(life.getMass());
      }
      // Moving average (i * avg + newVal) / i+1
      stat.setAvg((((stat.getCount()-1.0) * stat.getAvg()) + life.getMass()) / stat.getCount());
    } 
  }
  
  public void reset() {
    stats.clear();
  }
  
  public String toString() {
    StringBuffer str = new StringBuffer();
    for (String name : stats.keySet()) {
      Stat stat = stats.get(name);
      str.append("  "+stat+"\n");
    }
    return str.toString();
  }
  
}
