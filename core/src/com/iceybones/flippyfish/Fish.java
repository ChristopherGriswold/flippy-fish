package com.iceybones.flippyfish;

import com.badlogic.gdx.math.Vector3;

public class Fish extends GameObject{
  protected Vector3 velocity;
  protected boolean isInWater;
  protected boolean onSurface;
  protected static final int GRAVITY = -900;

  public Fish(int x, int y) {
    super(x, y);
  }

  public void update(float dt) {

  }

  protected void jump() {

  }

  protected Vector3 getVelocity() {
    return velocity;
  }

  protected void setVelocity(Vector3 velocity) {
    this.velocity = velocity;
  }
  protected void setVelocity(float x, float y, float z) {
    this.velocity.x = x;
    this.velocity.y = y;
    this.velocity.z = z;
  }

  protected boolean isInWater() {
    return isInWater;
  }
}
