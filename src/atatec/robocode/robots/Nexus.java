package atatec.robocode.robots;

import atatec.robocode.AbstractBot;
import atatec.robocode.Enemy;
import atatec.robocode.Field;
import atatec.robocode.annotation.When;
import atatec.robocode.behaviour.BulletPainter;
import atatec.robocode.behaviour.Dodger;
import atatec.robocode.calc.Point;
import atatec.robocode.event.EnemyFireEvent;
import atatec.robocode.event.Events;
import atatec.robocode.parts.aiming.PredictionAimingSystem;
import atatec.robocode.parts.firing.EnergyBasedFiringSystem;
import atatec.robocode.parts.movement.GravityMovingSystem;
import atatec.robocode.parts.scanner.EnemyLockScanningSystem;
import atatec.robocode.util.GravityPointBuilder;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;

import java.awt.Color;

import static atatec.robocode.event.Events.*;
import static atatec.robocode.condition.Conditions.headToHeadBattle;
import static atatec.robocode.util.GravityPointBuilder.antiGravityPoint;
import static atatec.robocode.util.GravityPointBuilder.gravityPoint;

/** @author Marcelo Varella Barca Guimarães */
public class Nexus extends AbstractBot {

  protected void configure() {
    body().setColor(new Color(39, 40, 34));
    gun().setColor(new Color(166, 226, 46));
    radar().setColor(new Color(39, 40, 34));

    moveAllPartsSeparated();

    gun().aimingBehaviour()
      .use(new PredictionAimingSystem(this));

    gun().firingBehaviour()
      .use(new EnergyBasedFiringSystem(this)
        .fireMaxAt(80)
        .fireMinAt(30));

    radar().scanningBehaviour()
      .use(new EnemyLockScanningSystem(this))
      .when(headToHeadBattle());

    radar().scanningBehaviour()
      .use(new EnemyLockScanningSystem(this).scanBattleField())
      .inOtherCases();

    body().movingBehaviour()
      .use(new GravityMovingSystem(this));

    behaveAs(new Dodger(this));
    behaveAs(new BulletPainter(this)
      .use(new Color(255, 84, 84)).forStrong()
      .use(new Color(253, 151, 31)).forMedium()
      .use(new Color(54, 151, 255)).forWeak());
  }

  @When(HIT_BY_BULLET)
  public void hitByBullet() {
    events().send(Events.ADD_GRAVITY_POINT,
      GravityPointBuilder
        .antiGravityPoint()
        .at(location())
        .withValue(10)
        .during(5)
    );
  }

  @When(ENEMY_FIRE)
  public void enemyFire(EnemyFireEvent event) {
    Enemy enemy = event.enemy();
    log("Enemy %s probably fired a bullet at %s. Adding anti-gravity point.",
      enemy.name(), enemy.position());
    events().send(Events.ADD_GRAVITY_POINT,
      antiGravityPoint()
        .at(enemy.location())
        .withValue(65)
        .during(20)
    );
    events().send(Events.ADD_GRAVITY_POINT,
      antiGravityPoint()
        .at(location())
        .withValue(20)
        .during(20)
    );
  }

  @When(HIT_ROBOT)
  public void hitHobot(HitRobotEvent event) {
    log("Adding anti-gravity points to avoid %s", event.getName());
    events().send(Events.ADD_GRAVITY_POINT,
      antiGravityPoint()
        .at(location())
        .withValue(30)
        .during(10)
    );
  }

  @When(HIT_WALL)
  public void onHitWall(HitWallEvent event) {
    log("Adding gravity point to avoid wall");
    Field battleField = radar().battleField();
    events().send(Events.ADD_GRAVITY_POINT,
      gravityPoint()
        .at(battleField.center())
        .withValue(100)
        .during(20)
    );
  }

  protected void battle() {
    addWallGravityPoints();
    while (true) {
      log("***********************************");
      addEnemyPoints();
      addRandomPoints();
      radar().scan();
      body().move();
      gun().aim();
      if (radar().lockedTarget() != null) {
        gun().fire();
      }
      execute();
    }
  }

  private void addWallGravityPoints() {
    Field battleField = radar().battleField();
    Point location = location();
    Point[] wallPoints = new Point[]{
      new Point(battleField.width(), location.y()),
      new Point(0, location.y()),
      new Point(location.x(), battleField.height()),
      new Point(location.x(), 0),
      new Point(location.x(), 0),
      battleField.upRight(),
      battleField.downLeft(),
      battleField.downRight(),
      battleField.upLeft(),
    };
    for (Point wallPoint : wallPoints) {
      events().send(Events.ADD_GRAVITY_POINT,
        wallPoint.antiGravitational().withValue(35)
      );
    }
  }

  private int midPointCount;
  private double midpointPower;

  private void addEnemyPoints() {
    for (Enemy enemy : radar().knownEnemies()) {
      events().send(Events.ADD_GRAVITY_POINT,
        enemy.location()
          .antiGravitational()
          .withValue(40)
          .during(20)
      );
    }
  }

  private void addRandomPoints() {
    Field battleField = radar().battleField();
    midPointCount++;
    if (midPointCount % 5 == 0) {
      midpointPower = (Math.random() * 20) - 10;
    }
    Point mod = new Point(
      (Math.random() * 30) - 15,
      (Math.random() * 30) - 15
    );
    events().send(Events.ADD_GRAVITY_POINT,
      battleField.center().plus(mod)
        .gravitational()
        .withValue(midpointPower)
        .during(1)
    );
  }

}
