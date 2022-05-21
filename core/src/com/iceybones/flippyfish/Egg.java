package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class Egg extends GameObject{
  public static int instances;
  private int instanceNumber;
  private Circle hitCircle;
  private int driftspeed;
  private static final int SPAWN_RATE = 10;
  private static final int POINT_VALUE = 1;
  private static Texture texture;
  private static Sound sound;
  private RandomXS128 random;

  public Egg() {
    instances++;
    instanceNumber = instances;
    random = new RandomXS128(FlippyFish.pickupSeed * instanceNumber);
    position = new Vector3(0, 0, 0);
    texture = new Texture("Images/egg.png");
    sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/pickup5.ogg"));
    hitCircle = new Circle(32, 32, 60);
    setOffScreen();
  }

  public void update(float dt, Player player) {
    if(isOffScreen) {
      waitOffscreen(dt);
      return;
    }
    if (position.x < -160) {
      setOffScreen();
      return;
    }
    if (position.x < 160) {
      if (hitCircle.overlaps(player.getHitCircle())) {
        FlippyFish.roundScore += POINT_VALUE;
        if (FlippyFish.sfxOn) {
          sound.play(.3f);
        }
        setOffScreen();
        return;
      }
    }

    position.x -= 200 * dt;
    drift(dt);
    hitCircle.setPosition(position.x + 32, position.y + 32);

  }


  @Override
  public void render(SpriteBatch sb) {
    if(isOffScreen) {
      return;
    }
    sb.draw(texture, position.x, position.y);
  }

  public void spawn() {
    position.x = 490;
    position.y = random.nextInt(420);
    driftspeed = random.nextInt(120) - 50;
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

  public void drift(float dt) {
    if (driftspeed == 0) {
      return;
    }
    if (position.y > 510) {
      driftspeed = 0;
      return;
    }
    position.y += (driftspeed * dt);
  }
}
