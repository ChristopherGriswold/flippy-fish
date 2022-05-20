package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

public class Player extends Fish {
  private Splash splash;
  private Circle hitCircle;
  private Ghost ghost;
  private PlayState playState;
  private long lastJumpTime;
  private Vector3 startPosition = new Vector3(16,600,0);
  private Vector3 startVelocity = new Vector3(0,0,0);
  private float bounceTimer;
  private long currentPressTime;
  public static Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/jump.ogg"));

  public Player(PlayState playState) {
    super(0, 0);
    texture = new Texture(Gdx.files.internal("Images/fish.png"));
    sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/swim.ogg"));
    this.playState = playState;
    hitCircle = new Circle(position.x + 80, position.y + 30, 20);
    splash = new Splash();
    ghost = new Ghost();
    position = startPosition;
    velocity = startVelocity;
    hitCircle.setPosition(position.x + 80, position.y + 30);
  }

  public void update(float dt) {
    if (playState.levelCleared) {
      hitCircle.setPosition(-500, -500);
      return;
    }

    if (FlippyFish.runningTime < FlippyFish.START_TIME_OFFSET) {
      return;
    }
    if(position.y < 540) {
      if (!isInWater) {
        onSurface = true;
        isInWater = true;
        splash.position.x = position.x + 80;
        splash.position.y = position.y;
        splash.position.z = position.z;
        splash.display(1f);
        if (FlippyFish.sfxOn) {
          splash.sound.play();
        }
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
        velocity.y += (GRAVITY * .75f * dt); // was /3
      } else {
        velocity.y += (GRAVITY * dt);
      }
    }
    position.y += velocity.y * dt;
    if (position.y < 0) {
      position.y = 0;
    }

    if (position.y < 3 ) {
      if (bounceTimer <= 0) {
        bounceTimer = .5f;
        jump();
      }
    }
    bounceTimer -= dt;
    splash.update(dt);
    hitCircle.setPosition(position.x + 80, position.y + 30);
  }

  @Override
  public void render(SpriteBatch sb) {
    sb.draw(texture, position.x, position.y, 80, 30, 128, 64, 1, 1,
            velocity.y / 10, 0, 0, 128, 64, false, false);
    splash.render(sb);
  }

  public void handleInput() {
    if (playState.levelCleared || !isInWater || (FlippyFish.runningTime < FlippyFish.START_TIME_OFFSET)) {
      return;
    }
      currentPressTime = FlippyFish.runningTime;
      if (currentPressTime - lastJumpTime < FlippyFish.DOUBLE_CLICK_SPEED) {
        return;
      }
      jump();
  }


  public void jump() {
    if (position.y > 500) {
      if (velocity.y < 2) {
        velocity.y = 600;
      } else {
        velocity.y = 700;
      }
      ghost.addJump(FlippyFish.runningTime, position.y, velocity.y);
      if (FlippyFish.sfxOn) {
        jumpSound.play(.5f);
      }
    }
    else {
      if (velocity.y < -200) {
        velocity.y = 100;
      } else if (velocity.y > -200) {
        velocity.y += 300;
      }
       if (velocity.y > 700) {
         velocity.y = 700;
       }
    }
    ghost.addJump(FlippyFish.runningTime, position.y, velocity.y);
    if (FlippyFish.sfxOn) {
      sound.play();
     // sound.play(1f, (position.y / 1000) + .4f, 0f);
    }
    lastJumpTime = currentPressTime;
  }

  public void handleGhost() {
    if (!playState.levelCleared) {
      ghost.setDeathTimeStamp(FlippyFish.runningTime);
    }
    if(FlippyFish.roundScore > FlippyFish.highScore) {
      // NEW HIGH SCORE. Playstate ghost = this ghost.
      FlippyFish.highScore = FlippyFish.roundScore;
      playState.getGhostFish().getGhost().cloneGhost(ghost);
    }
    else {
      // NOT A HIGH SCORE. Reset this ghost and playstate jumpcounter.
      playState.getGhostFish().getGhost().resetJumpIndex();
    }
    if (playState.isReplay && FlippyFish.roundScore > FlippyFish.highScoreSinceCompleted) {
      playState.getGhostFish().getGhost().cloneGhost(ghost);
      FlippyFish.saveHighScoreSinceCompleted(FlippyFish.roundScore);
      playState.getGhostFish().getGhost().saveGhost();
    }
    if ((!playState.isReplay && FlippyFish.roundScore >= 50) || playState.isBackButtonPressed()) {
      playState.getGhostFish().getGhost().saveGhost();
      FlippyFish.saveScore();
    } else {
      ghost.clearGhost();
      playState.restart();
    }
  }


  public void expire() {
    handleGhost();
    lastJumpTime = 0;
    currentPressTime = 0;
    splash.display(0);
  }



  public Vector3 getVelocity() {
    return velocity;
  }

  public void setVelocity(Vector3 velocity) {
    this.velocity = velocity;
  }
  public void setVelocity(float x, float y, float z) {
    this.velocity.x = x;
    this.velocity.y = y;
    this.velocity.z = z;
  }

  public boolean isInWater() {
    return isInWater;
  }
  public void setSplash(Splash splash) {
    this.splash = splash;
  }

  public Circle getHitCircle() {
    return hitCircle;
  }


  public Ghost getGhost() {
    return ghost;
  }

  public Splash getSplash() {
    return splash;
  }

  public class Splash extends GameObject {

    private float displayTime;
    private boolean isDisplaying;

    public Splash() {
      this(0,0);
    }
    public Splash(int x, int y) {
      super(x, y);
      texture = new Texture(Gdx.files.internal("Images/splashSmall.png"));
      sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/splashFast.ogg"));
      isDisplaying = false;
    }

    @Override
    public void render(SpriteBatch sb) {
      if(isDisplaying && !playState.levelCleared) {
        sb.draw(texture, position.x, position.y);
      }
    }


    public void update(float dt) {
      if (!isDisplaying || playState.levelCleared) {
        return;
      }
      if (displayTime <= 0) {
        isDisplaying = false;
      }
      position.x -= 200 * dt;
      displayTime -= dt;
    }

    public boolean isDisplaying() {
      return isDisplaying;
    }

    public void display(float seconds) {
      if (seconds == 0) {
        displayTime = 0;
        isDisplaying = false;
        return;
      }
      displayTime = seconds;
      isDisplaying = true;
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    jumpSound.dispose();
  }
}
