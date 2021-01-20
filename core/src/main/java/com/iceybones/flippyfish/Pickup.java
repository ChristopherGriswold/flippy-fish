package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class Pickup extends GameObject{
  public static int instances;
  private int instanceNumber;
  private Circle hitCircle;
  public static final int POINT_VALUE = 2;
  private static final int SPAWN_RATE = 30;
  private static Texture texture;
  private static Sound sound;
  private RandomXS128 random;

  public Pickup() {
    instances++;
    instanceNumber = instances;
    random = new RandomXS128(FlippyFish.pickupSeed * instanceNumber);
    position = new Vector3(0, 0, 0);
    texture = new Texture("Images/pickup.png");
    sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/pickup.ogg"));
    hitCircle = new Circle(64, 64, 110);
    setOffScreen();
  }

  public void update(float dt, Player player) {
    if(isOffScreen) {
      waitOffscreen(dt);
      return;
    }
    position.x -= 200 * dt;
    hitCircle.setPosition(position.x + 64, position.y + 64);
    if (position.x < -160) {
      setOffScreen();
      return;
    }

    if (position.x < 160) {
      if (hitCircle.overlaps(player.getHitCircle())) {
        FlippyFish.roundScore += POINT_VALUE;
        if (FlippyFish.sfxOn) {
          sound.play(.7f);
        }
        setOffScreen();
      }
    }

  }

  @Override
  public void render(SpriteBatch sb) {
    if(isOffScreen) {
      return;
    }
    sb.draw(texture, position.x, position.y, 64, 64, 128, 128,
            1, 1, position.x / 10, 0, 0, 128, 128, false, false);
  }

  public void spawn() {
    position.x = 490;
    position.y = random.nextInt(170) + 585;
  }

  public void waitOffscreen(float dt) {
    if (offScreenTimer > 0) {
      isOffScreen = true;
      offScreenTimer -= dt;
    } else {
      isOffScreen = false;
      spawn();
    }
  }

public void setOffScreen() {
  isOffScreen = true;
  offScreenTimer = random.nextInt(SPAWN_RATE) + (position.x + 160) / 200;
}

  public void resetRandom() {
    random.setSeed(FlippyFish.pickupSeed * instanceNumber);
  }

  public Circle getHitCircle() {
    return hitCircle;
  }
}
