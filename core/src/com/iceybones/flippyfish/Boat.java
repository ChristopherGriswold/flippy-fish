package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;

public class Boat extends GameObject {
  private Rectangle hitBox;
  private RandomXS128 random;

  public Boat() {
    super(0, 520);
    random = new RandomXS128(FlippyFish.boatSeed);
    texture = new Texture(Gdx.files.internal("Images/boat.png"));
    sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/fail.ogg"));
    hitBox = new Rectangle(54, 22, 78, 36);
    setOffScreen();
  }

  public void update(float dt, Player player) {
    if(isOffScreen) {
      waitOffscreen(dt);
      return;
    }
    position.x -= 200 * dt;
    hitBox.setPosition(position.x + 54, position.y + 22);
    if (position.x < -160) {
      setOffScreen();
      return;
    }

    if(position.x < 144) {
      if (Intersector.overlaps(player.getHitCircle(), hitBox)) {
        if (FlippyFish.sfxOn) {
          sound.play(.5f);
        }
        setOffScreen();
        player.expire();
      }
    }
  }

  public void spawn() {
    position.x = 490;
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
    offScreenTimer = (float) (10 / FlippyFish.level) + (random.nextInt(5) + 1);
  }

  public void resetRandom() {
    random.setSeed(FlippyFish.boatSeed);
  }
//
//  public void waitOffscreen() {
//    if ((TimeUtils.nanoTime() - offScreenStartTime) > offScreenTimer) {
//      isOffScreen = false;
//      spawn();
//    }
//  }


}
