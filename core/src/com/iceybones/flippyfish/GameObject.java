package com.iceybones.flippyfish;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class GameObject implements Disposable {
  protected Vector3 position;
  protected Texture texture;
  protected Sound sound;
  protected boolean isOffScreen;
  protected boolean isNotUpdated;
  protected float offScreenTimer;





  public GameObject() {
    position = new Vector3(0,0,0);
    isOffScreen = false;
  }
  public GameObject(int x, int y) {
    position = new Vector3(x, y, 0);
  }

  public void render(SpriteBatch sb)  {
    if(isOffScreen) {
      return;
    }
    sb.draw(texture, position.x, position.y);
  }

  public void update(float dt) {
    if (isNotUpdated) {
      return;
    }
  }
  public Vector3 getPosition() {
    return position;
  }
  public void setPosition(Vector3 position) {
    this.position = position;
  }
  public void setPosition(int x, int y, int z) {
    this.position.x = x;
    this.position.y = y;
    this.position.z = z;
  }

  public void setPosition(float x, float y, float z) {
    this.position.x = x;
    this.position.y = y;
    this.position.z = z;
  }
  public void setPosition(float x) {
    this.position.x = x;
  }

  public Texture getTexture() {
    if (texture != null) {
      return texture;
    } else {
      return null;
    }
  }

  public Sound getSound() {
    if (sound != null) {
      return sound;
    } else {
      return null;
    }
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  public void setSound(Sound sound) {
    this.sound = sound;
  }
  public void playSound() {
   // playSound();
    if (FlippyFish.sfxOn) {
      sound.play();
//    }
  }

  }

  @Override
  public void dispose() {
    if (texture != null) {
      texture.dispose();
    }
    if (sound != null) {
      sound.dispose();
    }
  }
}
