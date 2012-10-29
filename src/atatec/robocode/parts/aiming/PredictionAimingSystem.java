package atatec.robocode.parts.aiming;

import atatec.robocode.Bot;
import atatec.robocode.Enemy;
import atatec.robocode.Field;
import atatec.robocode.calc.Angle;
import atatec.robocode.parts.AimingSystem;
import robocode.Rules;
import robocode.util.Utils;

import java.awt.geom.Point2D;

/** @author Marcelo Varella Barca Guimarães */
public class PredictionAimingSystem implements AimingSystem {

  private final Bot bot;

  public PredictionAimingSystem(Bot bot) {
    this.bot = bot;
  }

  public void aim() {
    double bulletSpeed = Rules.getBulletSpeed(bot.gun().power());
    double myX = bot.location().x();
    double myY = bot.location().y();
    Enemy enemy = bot.radar().lockedTarget();
    if (enemy != null) {
      double enemyX = enemy.location().x();
      double enemyY = enemy.location().y();
      Angle enemyHeading = enemy.heading();
      double enemyVelocity = enemy.velocity();

      double deltaTime = 0;
      Field battleField = bot.radar().battleField();
      double battleFieldHeight = battleField.height(),
        battleFieldWidth = battleField.width();
      double predictedX = enemyX, predictedY = enemyY;
      while ((++deltaTime) * bulletSpeed <
        Point2D.Double.distance(myX, myY, predictedX, predictedY)) {
        predictedX += enemyHeading.sin() * enemyVelocity;
        predictedY += enemyHeading.cos() * enemyVelocity;
        if (predictedX < 18.0
          || predictedY < 18.0
          || predictedX > battleFieldWidth - 18.0
          || predictedY > battleFieldHeight - 18.0) {
          predictedX = Math.min(Math.max(18.0, predictedX),
            battleFieldWidth - 18.0);
          predictedY = Math.min(Math.max(18.0, predictedY),
            battleFieldHeight - 18.0);
          break;
        }
      }
      double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - myX, predictedY - myY));

      bot.gun().turn(new Angle(Utils.normalRelativeAngle(theta - bot.gun().heading().radians())));
    }
  }

}
