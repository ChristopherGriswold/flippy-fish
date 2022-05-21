package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class StarManager implements Disposable {
  private static Texture starFlash = new Texture("Images/flash2.png");
  public static float flashTimer;
  public static float animateTimer;
  public static float flashOffset;
  public static float flashSize;
  private boolean animationStarted;
  public static Vector2 starPosition = new Vector2(344f, 760f);
  public static Vector2 starSize = new Vector2(128f, 32f);
  public static Vector2 endStarPos = new Vector2(115, 40);
  public static Vector2 endStarSize = new Vector2(256f, 64f);
  private static Texture stars = new Texture("Images/noStars.png");
  private static Texture noStars = new Texture("Images/noStars.png");
  private static Texture oneStar = new Texture("Images/oneStar.png");
  private static Texture twoStars = new Texture("Images/twoStars.png");
  private static Texture threeStars = new Texture("Images/threeStars.png");
  private static int numStars;
  private static int starOneScore = 25;
  private static int starTwoScore = 35;
  private static int starThreeScore = 50;
  private static Sound sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/star.ogg"));
  private static Sound winSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/win.ogg"));

  public void render(SpriteBatch sb) {
    if (FlippyFish.isSurvivalMode) {
      return;
    }
    flash(sb);
    sb.draw(stars, starPosition.x, starPosition.y, starSize.x, starSize.y);
  }

  private Vector2 animPos = new Vector2();
  private Vector2 animSize = new Vector2();

  public void animate(SpriteBatch sb) {
    if (FlippyFish.roundScore < 50) {
      animationStarted = false;
      return;
    }
    if (!animationStarted)  {
      animPos.x = starPosition.x;
      animPos.y = starPosition.y;
      animSize.x = starSize.x;
      animSize.y = starSize.y;
      animationStarted = true;
    }
    float deltaTime = Gdx.graphics.getDeltaTime();
    animPos.x += (endStarPos.x - animPos.x) * deltaTime;
    animPos.y += (endStarPos.y - animPos.y) * deltaTime;
    animSize.x += (endStarSize.x - animSize.x) * deltaTime;
    animSize.y += (endStarSize.y - animSize.y) * deltaTime;
    if (animPos.y - endStarPos.y < 5) {
      animPos.x = endStarPos.x;
      animPos.y = endStarPos.y;
    } if (animSize.y - starSize.y < 2) {
      animSize.x = endStarSize.x;
      animSize.y = endStarSize.y;
    }
    sb.draw(stars, animPos.x, animPos.y, animSize.x,
        animSize.y);
  }


  public void flash(SpriteBatch sb) {
    if(flashTimer > 0) {
      if (FlippyFish.roundScore >= starThreeScore) {
        flashOffset = 40;
      } else if (FlippyFish.roundScore >= starTwoScore) {
        flashOffset = 0;
      } else {
        flashOffset = -40;
      }
      flashTimer -= Gdx.graphics.getRawDeltaTime();
      sb.draw(starFlash, 345 + flashOffset, 710, 64, 64, 128, 128,  // this used to be 128
          flashSize, flashSize,
          0, 0, 0, 512, 512, false, false);
      flashSize += .01f;


    } else flashSize = 0;
  }


  public static void setStars(int num) {
    numStars = num;
    switch (num) {
      case 1:
        stars = oneStar;
        break;
      case 2:
        stars = twoStars;
        break;
      case 3:
        stars = threeStars;
        break;
      default:
        stars = noStars;
        break;
    }
  }

  private static void addStar() {
    if (FlippyFish.sfxOn) {
      sound.play(.7f);
    }
    numStars++;
    flashTimer = 2f;
    if (numStars == 3) {
      if (FlippyFish.sfxOn) {
        winSound.play(.7f);
      }
    }
  }

  public void update(float dt) {
    if (FlippyFish.isSurvivalMode || numStars == 3) {
      return;
    } else if (FlippyFish.roundScore >= starThreeScore && numStars == 2) {
      stars = threeStars;
      addStar();
    } else if (FlippyFish.roundScore >= starTwoScore && numStars == 1) {
      stars = twoStars;
      addStar();
    } else if (FlippyFish.roundScore >= starOneScore && numStars == 0) {
      stars = oneStar;
      addStar();
    }
  }

  public static void checkScore() {
    if (numStars == 3) {
      return;
    } else if (FlippyFish.roundScore >= starThreeScore && numStars == 2) {
      stars = threeStars;
      addStar();
    } else if (FlippyFish.roundScore >= starTwoScore && numStars == 1) {
      stars = twoStars;
      addStar();
    } else if (FlippyFish.roundScore >= starOneScore && numStars == 0) {
      stars = oneStar;
      addStar();
    }
  }

  public static Texture getTexture() {
    return stars;
  }

  public static boolean checkScore(int level) {
      if(FlippyFish.scores.contains(Integer.toString(level) + ":h")) {
        int score = FlippyFish.scores.getInteger(Integer.toString(level) + ":h");
        if (score >= starThreeScore) {
          setStars(3);
          return true;
        } else if (score >= starTwoScore) {
          setStars(2);
          return true;
        } else if (score >= starOneScore) {
          setStars(1);
          return true;
        } else {
          setStars(0);
          if (level == 0) {
            return true;
          } else {
            return false;
          }
        }
      } else {
        setStars(0);
        if (level == 0) {
          return true;
        } else {
          return false;
        }
      }
    }

  public boolean isAnimationStarted() {
    return animationStarted;
  }

  public void setAnimationStarted(boolean animationStarted) {
    this.animationStarted = animationStarted;
  }

  public static Texture getNoStars() {
    return noStars;
  }

  @Override
  public void dispose() {
    starFlash.dispose();
    stars.dispose();
    noStars.dispose();
    oneStar.dispose();
    twoStars.dispose();
    threeStars.dispose();
    sound.dispose();
    winSound.dispose();
  }
}
