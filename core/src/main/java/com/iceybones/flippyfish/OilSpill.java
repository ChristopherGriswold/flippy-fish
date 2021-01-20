package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class OilSpill extends  GameObject{
  public static int instances;
  private Circle hitCircle;
  private int tempRandom;
  private int initialSpacing = 1000;
  private static Texture texture;
  private static Sound sound;
  private int startFrame;
  private int frameCounter;
  private int instanceNumber;
  private RandomXS128 random;

  public OilSpill() {
    instances++;
    instanceNumber = instances;
    random = new RandomXS128(FlippyFish.oilSpillSeed * instanceNumber);
    position = new Vector3(0, 0, 0);
    texture = new Texture("Images/oilSpill.png");
    sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/fail.ogg"));
    hitCircle = new Circle(64, 64, 42);
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

    if (position.x < 144) {
    if (hitCircle.overlaps(player.getHitCircle())) {
      player.expire();
      if (FlippyFish.sfxOn) {
        sound.play(.7f);
      }
    }
  }

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
    tempRandom = (random.nextInt(5) + 1) % 5;
    if (tempRandom < 2) {
      // Spawn high
      position.y = random.nextInt(25) + 390;
    } else if (tempRandom < 4) {
      // Spawn middle
      position.y = random.nextInt(150) + 110;
    }
    else {
      // Spawn low
      position.y = random.nextInt(80) - 90;
    }
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

//  public void waitOffscreen(float dt) {
//    if ((TimeUtils.nanoTime() - offScreenStartTime) > offScreenTimer) {
//      isOffScreen = false;
//      spawn();
//    }
//  }

//  public void waitOffscreen() {
//    while (frameCounter > 0) {
//      frameCounter--;
//      return;
//    }
//    isOffScreen = false;
//    spawn();
//  }

  public void setOffScreen() {
    isOffScreen = true;
    offScreenTimer = random.nextFloat() * 5f + 1;
  }

  public Circle getHitCircle() {
    return hitCircle;
  }
  public Texture getTexture() {
    return texture;
  }

  public void resetRandom() {
    random.setSeed(FlippyFish.oilSpillSeed * instanceNumber);
  }

}
