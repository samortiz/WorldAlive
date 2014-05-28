package com.alwaysrejoice.worldalive.ai;

import com.alwaysrejoice.worldalive.Const;

public class GrassAI extends BaseAI {

  
  public GrassAI() {
    this.massToStartReproducing = ((Const.MIN_MASS+1)*10) + 150;
    this.litterSize = 1;
    this.spawnDistance = 10;
    this.massToFetus = Const.MIN_MASS + 1;
    this.birthMass = Const.MIN_MASS;
  }
  
}
