package atatec.robocode.parts;

import atatec.robocode.Conditional;
import atatec.robocode.calc.Point;

import java.awt.Color;

/** @author Marcelo Varella Barca Guimarães */
public interface Gun extends Part, AimingSystem, FiringSystem {

  Gun aimTo(Point point);

  void fire(double power);

  double heat();

  double coolingRate();

  Conditional<AimingSystem> aimingBehaviour();

  Conditional<FiringSystem> firingBehaviour();

  void setBulletColor(Color color);

}
