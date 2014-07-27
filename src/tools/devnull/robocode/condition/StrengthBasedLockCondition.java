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

package tools.devnull.robocode.condition;

import tools.devnull.robocode.Bot;
import tools.devnull.robocode.Enemy;
import tools.devnull.robocode.annotation.When;
import tools.devnull.robocode.calc.Point;
import tools.devnull.robocode.event.Events;
import tools.devnull.robocode.util.Drawer;

import java.awt.*;
import java.util.Collection;

/**
 * @author Marcelo Guimarães
 */
public class StrengthBasedLockCondition implements LockCondition {

  private enum LockMode {
    STRONGER {
      @Override
      public boolean canLock(double lockedStr, double candidateStr) {
        return candidateStr > lockedStr;
      }
    },
    WEAKER {
      @Override
      public boolean canLock(double lockedStr, double candidateStr) {
        return candidateStr < lockedStr;
      }
    };

    public abstract boolean canLock(double lockedStr, double candidateStr);
  }

  private final Bot bot;
  private LockMode mode = LockMode.STRONGER;
  private Function<Enemy, Double> strengthFunction = new Function<Enemy, Double>() {
    @Override
    public Double evaluate(Enemy argument) {
      return argument.energy();
    }
  };

  public StrengthBasedLockCondition(Bot bot) {
    this.bot = bot;
  }

  public StrengthBasedLockCondition use(Function<Enemy, Double> strengthFunction) {
    this.strengthFunction = strengthFunction;
    return this;
  }

  public StrengthBasedLockCondition lockWeaker() {
    this.mode = LockMode.WEAKER;
    return this;
  }

  public StrengthBasedLockCondition lockStronger() {
    this.mode = LockMode.STRONGER;
    return this;
  }

  @Override
  public boolean canLock(Enemy enemy) {
    double candidateEnemyStr = strengthFunction.evaluate(enemy);
    double lockedEnemyStr = strengthFunction.evaluate(bot.radar().target());
    return mode.canLock(lockedEnemyStr, candidateEnemyStr);
  }

  @When(Events.DRAW)
  public void drawStrength(Drawer drawer) {
    Collection<Enemy> enemies = bot.radar().knownEnemies();
    Point point;
    for (Enemy enemy : enemies) {
      point = enemy.location().right(25);
      if (!bot.radar().battleField().isOnField(point.right(30))) {
        point = enemy.location().left(60);
      }
      drawer.draw(Color.RED
      ).string("%.3f", strengthFunction.evaluate(enemy)).at(point);
    }
  }

}