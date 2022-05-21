package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;

public class PlayState extends State {

  private StateManager stateManager;
  public static Sound themeSong;
  public boolean levelCleared;
  public boolean isReplay;
  private float displayTime = 5f;
  private float animateStarTime = 1f;
  public boolean backButtonPressed;
  private Boat boat;
  private Player player;
  private GhostFish ghostFish;
  private Wave wave;
  private Texture background;
  private Texture backButton;
  private Texture levelCompleted;
  private float levelClearedDisplayTime = 10f;
  private Sound backSound;
  private int numPickups = 2;
  private int numEggs = 3;
  private int numOilSpills;
  private ArrayList<Pickup> pickupList;
  private ArrayList<Egg> eggList;
  private ArrayList<OilSpill> oilSpillList;
  private static StarManager starManager;
  BitmapFont font;
  private long numUpdates = 0;
  private Boolean isResetting = false;


  public PlayState(StateManager stateManager) {
    super(stateManager);
    this.stateManager = stateManager;
    starManager = new StarManager();
    themeSong = Gdx.audio.newSound(Gdx.files.internal("Sounds/fishThemeQuiet.ogg"));
    backSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/pickup2.ogg"));
    backButton = new Texture("Images/backButton.png");
    background = new Texture("Images/background.png");
    levelCompleted = new Texture("Images/levelCleared.png");
    fontHandler();
    player = new Player(this);
    boat = new Boat();
    wave = new Wave(0, 530);
    pickupList = new ArrayList<Pickup>();
    eggList = new ArrayList<Egg>();
    oilSpillList = new ArrayList<OilSpill>();
    ghostFish = new GhostFish();

    for (int i = 0; i < numPickups; i++) {
      pickupList.add(new Pickup());
    }
    for (int i = 0; i < numEggs; i++) {
      eggList.add(new Egg());
    }
  }


  @Override
  protected void start() {
    if (FlippyFish.soundOn) {
      themeSong.stop();
      themeSong.loop();
    }
    levelCleared = false;
    backButtonPressed = false;
    FlippyFish.loadScore();
    FlippyFish.highScoreSinceCompleted = FlippyFish.getHighScoreSinceCompleted();
    if (FlippyFish.checkLevelCompleted()) {
      isReplay = true;
      numOilSpills = (FlippyFish.getLevel() * 2) + 2;
    } else {
      isReplay = false;
      numOilSpills = FlippyFish.getLevel() + 2;
    }
    int numSpillsToAdd = numOilSpills - oilSpillList.size();
    if (numSpillsToAdd > 0) {
      for (int i = 0; i < numSpillsToAdd; i++) {
        oilSpillList.add(new OilSpill());
      }
    } else if (numSpillsToAdd < 0) {
      while (numSpillsToAdd < 0) {
        oilSpillList.remove(oilSpillList.size() - 1);
        OilSpill.instances--;
        numSpillsToAdd++;
      }
    }
    ghostFish.initializeGhost();
    restart();
    }

  public void restart() {
      player.setPosition(16, 600, 0);
      player.setVelocity(0,0,0);
      player.getSplash().display(0);
      wave.setPosition(0, 530, 0);
      boat.resetRandom();
      boat.setOffScreen();

      for (int i = 0; i < numPickups; i++) {
        pickupList.get(i).setPosition(0,0,0 );
        pickupList.get(i).resetRandom();
        pickupList.get(i).setOffScreen();
      }
      for (int i = 0; i < numEggs; i++) {
        eggList.get(i).setPosition(0,0,0 );
        eggList.get(i).resetRandom();
        eggList.get(i).setOffScreen();
      }
    if (FlippyFish.isSurvivalMode) {
      OilSpill.instances = 0;
      oilSpillList = new ArrayList<OilSpill>();
      numOilSpills = 2;
      for (int i = 0; i < numOilSpills; i++) {
        oilSpillList.add(new OilSpill());
      }
    } else {
      for (int i = 0; i < numOilSpills; i++) {
        oilSpillList.get(i).resetRandom();
        oilSpillList.get(i).setOffScreen();
      }
    }
    FlippyFish.runningTime = 0;
    FlippyFish.roundScore = 0;
    FlippyFish.gameStartTime = TimeUtils.nanoTime();
    ghostFish.start();
    isResetting = false;
  }


  @Override
  public void handleInput() {
    if (Gdx.input.justTouched()) {
      int x = Gdx.input.getX();
      int y = Gdx.input.getY();
      if (x < 144 && y < 128 && FlippyFish.runningTime > 1_000_000_000) {
        // Back button pressed
        backButtonPressed = true;
        goToMenu();
        return;
      }
      player.handleInput();
    }
  }


  @Override
  public void update(float dt) {
    if (FlippyFish.isSurvivalMode && (FlippyFish.runningTime / 10_000_000_000L) + 2 > oilSpillList.size()) {
      OilSpill.instances++;
      numOilSpills++;
      oilSpillList.add(new OilSpill());
    }
    player.update(dt);
    ghostFish.update(dt, player);
    starManager.update(dt);
    wave.update(dt);
    boat.update(dt, player);

    for (Pickup pickup : pickupList) {
      pickup.update(dt, player);
    }
    for (Egg egg : eggList) {
      egg.update(dt, player);
    }
    for (OilSpill oilSpill : oilSpillList) {
      oilSpill.update(dt, player);
    }

    if (levelCleared) {
      themeSong.stop();
    }
  }

  @Override
  public void render(SpriteBatch sb) {
    sb.begin();
    sb.draw(background, 0, 0, FlippyFish.WIDTH, FlippyFish.HEIGHT);
    wave.render(sb);
    for (Pickup pickup : pickupList) {
      pickup.render(sb);
    }
    for (Egg egg : eggList) {
      egg.render(sb);
    }
    for (OilSpill oilSpill : oilSpillList) {
      oilSpill.render(sb);
    }
    ghostFish.render(sb);
    player.render(sb);
    boat.render(sb);

    if (!levelCleared) {
      if (ghostFish.isAlive()) {
        if((ghostFish.getGhost().getDeathTimeStamp() - FlippyFish.runningTime) < 5_000_000_000L) {
          font.draw(sb, "Ghost is about to expire!", 64, 64);
        }
      }
      if (FlippyFish.runningTime > 1_000_000_000) {
        sb.draw(backButton, 8, 736, 64, 64);
      }
    }

//    font.draw(sb, "" + FlippyFish.runningTime / 10_000_000_000L,
//        365 , 750);
    if (FlippyFish.isSurvivalMode) {
      font.draw(sb, FlippyFish.roundScore + " / " + FlippyFish.highScore,
          340 , 780);
    } else {
      font.draw(sb, FlippyFish.roundScore + " / " + (Math.max(FlippyFish.highScore, 50)),
          365 , 750);
    }

    displayLevelCleared(sb);
    starManager.render(sb);
    sb.end();
  }


  private void fontHandler() {
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts"
        + "/Ubuntu-MediumItalic.ttf"));
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = 30;
    parameter.color = new Color(.469f, .421f, .98f, 1f);
    font = generator.generateFont(parameter); // font size 12 pixels
    generator.dispose(); // don't forget to dispose to avoid memory leaks!

  }

  public GhostFish getGhostFish() {
    return ghostFish;
  }

  public void setGhostFish(GhostFish ghostFish) {
    this.ghostFish = ghostFish;
  }

  public StateManager getStateManager() {
    return stateManager;
  }

  public Sound getBackSound() {
    return backSound;
  }

  public boolean isBackButtonPressed() {
    return backButtonPressed;
  }

  @Override
  public void dispose () {
    boat.dispose();
    player.dispose();
    wave.dispose();
    background.dispose();
    ghostFish.dispose();
    backSound.dispose();
    themeSong.dispose();
    levelCompleted.dispose();
    font.dispose();
    for (Pickup pickup : pickupList) {
      pickup.dispose();
    }
    for (OilSpill oilSpill : oilSpillList) {
      oilSpill.dispose();
    }
    for (Egg egg : eggList) {
      egg.dispose();
    }
  }

  public void goToMenu() {
    themeSong.stop();
    if (FlippyFish.sfxOn) {
      backSound.play();
    }
    player.expire();
    if (levelCleared) {
      FlippyFish.setLevelCompleted();
      player.getGhost().clearGhost();
      player.getGhost().saveGhost();
    }
 // dispose();
    stateManager.setState(0);
  }

  private boolean lvlClearedFlag;

  public void displayLevelCleared(SpriteBatch sb) {
    if (!FlippyFish.isSurvivalMode && FlippyFish.roundScore >= 50 && displayTime > 0 && !isReplay) {
      sb.draw(levelCompleted, 0, 0);
   //   sb.draw(StarManager.getNoStars(), 115, 40);
      displayTime -= Gdx.graphics.getDeltaTime();
      starManager.animate(sb);
      levelCleared = true;
    }
    if (levelCleared && !lvlClearedFlag) {
      player.getGhost().setDeathTimeStamp(FlippyFish.runningTime);
      lvlClearedFlag = true;
    }
    if (displayTime < 0) {
      displayTime = 5f;
      starManager.setAnimationStarted(false);
      goToMenu();
      starManager.setAnimationStarted(false);
      lvlClearedFlag = false;
    }
  }

  public Player getPlayer() {
    return player;
  }
}
