/************************************************************************************
 * The MIT License                                                                  *
 *                                                                                  *
 * Copyright (c) 2013 Marcelo Guimarães <ataxexe at gmail dot com>                  *
 * -------------------------------------------------------------------------------- *
 * Permission  is hereby granted, free of charge, to any person obtaining a copy of *
 * this  software  and  associated documentation files (the "Software"), to deal in *
 * the  Software  without  restriction,  including without limitation the rights to *
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of *
 * the  Software, and to permit persons to whom the Software is furnished to do so, *
 * subject to the following conditions:                                             *
 *                                                                                  *
 * The  above  copyright notice and this permission notice shall be included in all *
 * copies or substantial portions of the Software.                                  *
 *                            --------------------------                            *
 * THE  SOFTWARE  IS  PROVIDED  "AS  IS",  WITHOUT WARRANTY OF ANY KIND, EXPRESS OR *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS *
 * FOR  A  PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR *
 * COPYRIGHT  HOLDERS  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER *
 * IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM, OUT OF OR IN *
 * CONNECTION  WITH  THE  SOFTWARE  OR  THE  USE OR OTHER DEALINGS IN THE SOFTWARE. *
 ************************************************************************************/

package atatec.robocode.robots;

import atatec.robocode.BaseBot;
import atatec.robocode.annotation.When;
import atatec.robocode.plugin.BulletPaint;
import atatec.robocode.plugin.Dodger;
import atatec.robocode.event.EnemyFireEvent;
import atatec.robocode.event.Events;
import atatec.robocode.parts.aiming.PredictionAimingSystem;
import atatec.robocode.parts.firing.EnergyBasedFiringSystem;
import atatec.robocode.parts.scanner.EnemyLockScanningSystem;
import atatec.robocode.plugin.EnemyScannerInfo;

import java.awt.Color;

/** @author Marcelo Varella Barca Guimarães */
public class Newton extends BaseBot {

  @Override
  public void configure() {
    body().setColor(new Color(39, 40, 34));
    gun().setColor(new Color(230, 219, 116));
    radar().setColor(new Color(39, 40, 34));

    gun().aimingSystem()
      .use(new PredictionAimingSystem(this));

    gun().firingSystem()
      .use(new EnergyBasedFiringSystem(this));

    radar().scanningSystem()
      .use(new EnemyLockScanningSystem(this).lockClosestEnemy());

    plug(new Dodger(this));
    plug(new EnemyScannerInfo(this));
    plug(new BulletPaint(this)
      .use(new Color(255, 84, 84)).forStrong()
      .use(new Color(253, 151, 31)).forMedium()
      .use(new Color(54, 151, 255)).forWeak());
  }

  @When(Events.ENEMY_FIRE)
  public void onEnemyFire(EnemyFireEvent event) {
    body().moveAndTurn(100 * Math.pow(-1, radar().time()),
      event.enemy().bearing().inverse());
  }

  protected void onNextTurn() {
    gun().aim().fireIfTargetLocked();
    radar().scan();
  }

}