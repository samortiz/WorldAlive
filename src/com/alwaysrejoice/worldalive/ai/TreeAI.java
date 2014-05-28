package com.alwaysrejoice.worldalive.ai;

import com.alwaysrejoice.worldalive.Const;

public class TreeAI extends BaseAI {

  public TreeAI() {
    this.massToStartReproducing = 5000;
    this.litterSize = 1;
    this.spawnDistance = 30;
    this.massToFetus = Const.MIN_MASS + 1;
    this.birthMass = Const.MIN_MASS;
  }
    
}
