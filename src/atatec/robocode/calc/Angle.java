package atatec.robocode.calc;

import robocode.util.Utils;

import static atatec.robocode.calc.BotMath.areEquals;
import static atatec.robocode.calc.BotMath.toBigecimal;

/** @author Marcelo Varella Barca Guimarães */
public class Angle {

  public static final Angle ZERO = new Angle(0);

  public static final Angle PI = new Angle(Math.PI);

  public static final Angle TWO_PI = new Angle(Math.PI * 2);

  public static final Angle PI_OVER_TWO = new Angle(Math.PI / 2);

  public static final Angle MINUS_PI_OVER_TWO = new Angle(-Math.PI / 2);

  public static final Angle PI_OVER_FOUR = new Angle(Math.PI / 4);

  public static final Angle MINUS_PI_OVER_FOUR = new Angle(-Math.PI / 4);

  private final double radians;

  public Angle(double radians) {
    this.radians = radians;
  }

  public double radians() {
    return radians;
  }

  public double degrees() {
    return Math.toDegrees(radians);
  }

  public Angle plus(Angle angle) {
    return new Angle(radians + angle.radians);
  }

  public Angle plus(double angle) {
    return plus(new Angle(angle));
  }

  public Angle minus(Angle angle) {
    return new Angle(radians - angle.radians);
  }

  public Angle minus(double angle) {
    return minus(new Angle(angle));
  }

  public Angle inverse() {
    return new Angle(-radians);
  }

  public double cos() {
    return Math.cos(radians);
  }

  public double sin() {
    return Math.sin(radians);
  }

  public double tan() {
    return Math.tan(radians);
  }

  public Angle toRight() {
    return new Angle(Math.abs(radians));
  }

  public Angle toLeft() {
    return new Angle(-Math.abs(radians));
  }

  public boolean isNorth() {
    return radians >= MINUS_PI_OVER_TWO.radians() && radians <= PI_OVER_TWO.radians();
  }

  public boolean isSouth() {
    return !isNorth();
  }

  public boolean isEast() {
    return radians >= 0 && radians <= Math.PI;
  }

  public boolean isWest() {
    return !isEast();
  }

  public Angle relative() {
    return new Angle(Utils.normalRelativeAngle(radians));
  }

  public Angle absolute() {
    return new Angle(Utils.normalAbsoluteAngle(radians));
  }

  @Override
  public String toString() {
    return String.format("%.6fd", degrees());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Angle angle = (Angle) o;

    return areEquals(angle.radians(), radians());
  }

  @Override
  public int hashCode() {
    return toBigecimal(radians).hashCode();
  }

  public static Angle inDegrees(double degrees) {
    return new Angle(Math.toRadians(degrees));
  }

  public static Angle inRadians(double radians) {
    return new Angle(radians);
  }

}
