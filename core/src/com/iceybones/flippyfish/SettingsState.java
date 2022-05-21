package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;


public class SettingsState extends State{
  private Texture background;
  private Texture backButton;
  private Texture offEgg;
  private Texture egg;
  private Texture confirm;
  BitmapFont font;
  private Sound pop;
  private Sound popDown;
  private Sound settingsSound;
  private Sound clearDataSound;
  private boolean soundOn;
  private boolean sfxOn;
  private boolean dataCleared;
  private boolean isConfirming = false;

  Vector3 touchPoint;

  protected SettingsState(StateManager stateManager) {
    super(stateManager);
  settingsSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/pickup2.ogg"));
  pop = Gdx.audio.newSound(Gdx.files.internal("Sounds/pop.ogg"));
  popDown = Gdx.audio.newSound(Gdx.files.internal("Sounds/popDown.ogg"));
  clearDataSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/fail.ogg"));
  touchPoint = new Vector3();
  background = new Texture("Images/settingsMenu.png");
  egg = new Texture("Images/egg.png");
  offEgg = new Texture("Images/offEgg.png");
  backButton = new Texture("Images/backButton.png");
  confirm = new Texture("Images/confirm_button.png");
  soundOn = true;
  sfxOn = true;
}

  @Override
  public void handleInput() {
    if (Gdx.input.justTouched()) {
      FlippyFish.camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
      int x = (int) touchPoint.x;
      int y = (int) touchPoint.y;

      if (x < 80 && y > 720 && FlippyFish.runningTime > 1_000_000_000) {
        isConfirming = false;
        // Back button pressed
        if (FlippyFish.sfxOn) {
          settingsSound.play();
        }
        stateManager.setState(0);
        return;
      }

      if (x > 60 && x < 406) {
        if (y > 394 && y < 478) {
          // Sound button pressed
          isConfirming = false;
          if (!soundOn) {
            if (FlippyFish.sfxOn) {
              pop.play();
            }
            soundOn = true;
            FlippyFish.soundOn = true;
     //       ((MenuState) stateManager.getStates().get(0)).playMusic(true);
          } else {
            if (FlippyFish.sfxOn) {
              popDown.play();
            }
            soundOn = false;
            FlippyFish.soundOn = false;
    //        ((MenuState) stateManager.getStates().get(0)).playMusic(false);
          }
        } else if (y > 240 && y < 325) {
          // sfx button pressed
          isConfirming = false;
          if (!sfxOn) {
            pop.play();
            sfxOn = true;
            FlippyFish.sfxOn = true;
          } else {
            if (FlippyFish.sfxOn) {
              popDown.play();
            }
            sfxOn = false;
            FlippyFish.sfxOn = false;
          }
        }
        else if (y > 90 && y < 180) {
          // Clear data called
          if (dataCleared) {
            return;
          }
          if (!isConfirming){
            isConfirming = true;
            return;
          }
          if (FlippyFish.sfxOn) {
            clearDataSound.play(.5f);
          }
          FlippyFish.scores.clear();
          FlippyFish.scores.flush();
          FlippyFish.ghostJumps.clear();
          FlippyFish.ghostJumps.flush();
          dataCleared = true;
          isConfirming = false;
          FlippyFish.playerName = "CLICK HERE";
          FlippyFish.scores.putString("name", "CLICK HERE");
        }
      }
      isConfirming = false;
    }
  }

  @Override
  protected void start() {
    FlippyFish.runningTime = 0;
    dataCleared = false;
    FlippyFish.gameStartTime = TimeUtils.nanoTime();
  }

  @Override
  protected void update(float dt) {

  }

  private void launch() {
    // Back to menu
    stateManager.setState(0);
  }

  @Override
  public void render(SpriteBatch sb) {
    sb.begin();
    sb.draw(background, 0, 0, FlippyFish.WIDTH, FlippyFish.HEIGHT);
    if (isConfirming) {
      sb.draw(confirm, 66, 98);
    }
    if (FlippyFish.runningTime > 1_000_000_000) {
      sb.draw(backButton, 8, 736, 64, 64);
    }
    if(soundOn) {
      sb.draw(egg, 320, 404, 64, 64);
    } else {
      sb.draw(offEgg, 320, 404, 64, 64);
    }
    if(sfxOn) {
      sb.draw(egg, 320, 250, 64, 64);
    } else {
      sb.draw(offEgg, 320, 250, 64, 64);
    }
    sb.end();
  }

  @Override
  public void dispose() {

  }


}
