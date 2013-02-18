package atatec.robocode.plugin;

import atatec.robocode.Bot;
import atatec.robocode.annotation.When;
import atatec.robocode.event.BulletFiredEvent;
import atatec.robocode.event.Events;
import robocode.Rules;

import java.awt.Color;

import static robocode.Rules.MAX_BULLET_POWER;
import static robocode.Rules.MIN_BULLET_POWER;

/** @author Marcelo Varella Barca Guimarães */
public class BulletPaint {

  private static final double MEDIUM_BULLET_POWER = (MAX_BULLET_POWER + MIN_BULLET_POWER) / 2;

  private final Bot bot;

  private Color strongColor = Color.RED;
  private Color mediumColor = Color.BLUE;
  private Color weakColor = Color.ORANGE;

  private Color selected;

  public BulletPaint(Bot bot) {
    this.bot = bot;
  }

  public BulletPaint use(Color color) {
    this.selected = color;
    return this;
  }

  public BulletPaint forStrong() {
    this.strongColor = selected;
    return this;
  }
  public BulletPaint forMedium() {
    this.mediumColor = selected;
    return this;
  }

  public BulletPaint forWeak() {
    this.weakColor = selected;
    return this;
  }

  @When(Events.BULLET_FIRED)
  public void onBulletFired(BulletFiredEvent event) {
    double power = event.bullet().getPower();
    if (power >= Rules.MAX_BULLET_POWER) {
      bot.gun().setBulletColor(strongColor);
    } else if (power >= MEDIUM_BULLET_POWER) {
      bot.gun().setBulletColor(mediumColor);
    } else {
      bot.gun().setBulletColor(weakColor);
    }
  }

}
