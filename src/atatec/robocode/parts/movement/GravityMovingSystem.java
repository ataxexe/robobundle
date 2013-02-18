package atatec.robocode.parts.movement;

import atatec.robocode.Bot;
import atatec.robocode.annotation.When;
import atatec.robocode.calc.GravityPoint;
import atatec.robocode.calc.Point;
import atatec.robocode.calc.TemporaryGravityPoint;
import atatec.robocode.parts.MovingSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import static atatec.robocode.event.Events.ADD_GRAVITY_POINT;

/** @author Marcelo Varella Barca Guimarães */
public class GravityMovingSystem implements MovingSystem {

  private final Bot bot;
  private Collection<GravityPoint> fixedPoints = new HashSet<GravityPoint>(100);
  private Collection<TemporaryGravityPoint> temporaryPoints = new HashSet<TemporaryGravityPoint>(100);

  public GravityMovingSystem(Bot bot) {
    this.bot = bot;
  }

  @When(ADD_GRAVITY_POINT)
  public GravityMovingSystem add(GravityPoint point) {
    fixedPoints.add(point);
    return this;
  }

  @When(ADD_GRAVITY_POINT)
  public GravityMovingSystem add(TemporaryGravityPoint point) {
    temporaryPoints.add(point);
    return this;
  }

  @Override
  public void move() {
    Collection<GravityPoint> gPoints = new ArrayList<GravityPoint>(fixedPoints);
    Iterator<TemporaryGravityPoint> iterator = temporaryPoints.iterator();
    while (iterator.hasNext()) {
      TemporaryGravityPoint temporaryGravityPoint = iterator.next();
      GravityPoint point = temporaryGravityPoint.pull();
      if (point == null) {
        iterator.remove();
      } else {
        gPoints.add(point);
      }
    }
    move(gPoints);
  }

  private void move(Collection<GravityPoint> points) {
    Point location = bot.location();
    Point forcePoint = location;
    for (GravityPoint point : points) {
      forcePoint = forcePoint.plus(point.force(location));
    }
    bot.log("Location: %s", location);
    bot.log("Forced Location: %s", forcePoint);
    double move = 10;
    bot.body().moveTo(forcePoint, move);
  }

}
