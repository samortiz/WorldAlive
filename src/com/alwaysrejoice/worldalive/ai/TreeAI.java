package com.alwaysrejoice.worldalive.ai;

import com.alwaysrejoice.worldalive.Const;

public class TreeAI extends BaseAI {

  private static final long serialVersionUID = 1L;

  public TreeAI() {
    this.massToStartReproducing = 4000;
    this.litterSize = 1;
    this.spawnDistance = 20;
    this.massToFetus = 5;
    this.birthMass = Const.MIN_MASS+1;
  }
    
}
