package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class SplashScreen extends State{
  private Texture logo;
  private long displayTime = 2_000_000_000L; // ZERO FOR HTML ONLY
  private Music startSound;

  protected SplashScreen(StateManager stateManager) {
    super(stateManager);
    logo = new Texture("Images/logoBackground.png");
    startSound = Gdx.audio.newMusic(Gdx.files.internal("Sounds/pickup2.ogg"));
  }

  @Override
  protected void handleInput() {

  }

  @Override
  protected void start() {
    FlippyFish.gameStartTime = TimeUtils.nanoTime();
  }

  @Override
  protected void update(float dt) {
    while (FlippyFish.runningTime < displayTime) {
      return;
    }

    startSound.setVolume(.5f);
    startSound.play();
    stateManager.setState(0);
  }

  @Override
  protected void render(SpriteBatch sb) {
    sb.begin();
    sb.draw(logo, 0, 0, FlippyFish.WIDTH, FlippyFish.HEIGHT);
    sb.end();
  }

  @Override
  public void dispose() {

  }
}
