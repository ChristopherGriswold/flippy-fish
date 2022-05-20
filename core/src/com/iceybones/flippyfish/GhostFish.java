package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.iceybones.flippyfish.Ghost.Jump;

public class GhostFish extends Fish{
  private Vector3 splashPosition;
  private Vector3 startPosition = new Vector3(300, 600, 0);
  private boolean isAlive;
  private boolean isVisible;
  private Ghost ghost;
  private boolean waitingToJump;
  private Jump nextJumpObject;

  public GhostFish() {
    super(0, 0);
    texture = new Texture(Gdx.files.internal("Images/ghostFish.png"));
    velocity = new Vector3(0,0,0);
    splashPosition = new Vector3();
    isAlive = false;
  }

  public void start() {
    position.x = startPosition.x;
    position.y = startPosition.y;
    velocity.y = 0;
    waitingToJump = false;
 //   isAlive = true;
    isVisible = true;
  }

  public void update(float dt, Player player) {
    play(dt);
    if(!isVisible) {
      return;
    } else if (!isAlive) {
      position.x -= (200 * dt);
      if (position.x < -150) {
        isVisible = false;
      }
      return;
    }
    if(position.y < 530) {
      if (!isInWater) {
        onSurface = true;
      } else {
        onSurface = false;
      }
      isInWater = true;
    } else {
      isInWater = false;
      onSurface = false;
    }
    if (position.y > 0) {
      if (onSurface) {
        velocity.y = 0;
      }else if (isInWater) {
        velocity.y += GRAVITY * .75f * dt;
      } else {
        velocity.y += GRAVITY * dt;
      }
    }
    position.y += velocity.y * dt;
    if (position.y < 0) {
      position.y = 0;
    }
  }

  public void render(SpriteBatch sb) {
    if(isVisible && FlippyFish.highScore > 0 && (FlippyFish.highScoreSinceCompleted != 0)) {
      sb.draw(texture, position.x, position.y,
          80, 30, 128, 64, 1, 1, velocity.y / 10, 0, 0, 128, 64, false, false);
    }
  }


  public void jump(Jump jump) {
    position.y = jump.getPosY();
    velocity.y = jump.getVelY();
  }

  public Vector3 getVelocity() {
    return velocity;
  }

  public void setVelocity(float x, float y, float z) {
    this.velocity.x = x;
    this.velocity.y = y;
    this.velocity.z = z;
  }

  public boolean isInWater() {
    return isInWater;
  }


  public void setAlive(boolean alive) {
    isAlive = alive;
  }

  public boolean isAlive() {
    return isAlive;
  }

  private void play(float dt) {
    if((ghost.getDeathTimeStamp() - FlippyFish.START_TIME_OFFSET - FlippyFish.runningTime) < 0) {
      setAlive(false);
    } else {
      setAlive(true);
    }
    if (!waitingToJump) {
      nextJumpObject = ghost.getNextJump();
      waitingToJump = true;
    }
    if (nextJumpObject == null) {
      waitingToJump = true;
      return;
    }
      if ((nextJumpObject.getTime() - FlippyFish.START_TIME_OFFSET - FlippyFish.runningTime) < 0) {
        jump(nextJumpObject);
        waitingToJump = false;
      }
  }

  public Ghost getGhost() {
    return ghost;
  }

  public void setGhost(Ghost ghost) {
    this.ghost = ghost;
  }

  public void initializeGhost() {
    ghost = new Ghost();
    ghost.loadGhost();
  }
}
