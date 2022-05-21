package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MenuState extends State{
  private Texture background;
  private Texture backButton;
  private Texture arrowRight;
  private Texture arrowRightGray;
  private Texture arrowLeft;
  private Texture arrowLeftGray;
  private Texture fish;
  private Texture previewBorder;
  private Texture trophies;
  private Texture gold_trophy;
  private Texture silver_trophy;
  private Texture bronze_trophy;
  private Texture black_trophy;
  private Texture egg;
  private ArrayList<Texture> ghostLayers;

  private float ghostCycle = .3f;
  private int currentGhostLayer;
  BitmapFont font;
  BitmapFont smallFont;
  BitmapFont leaderboardFont;
  private ArrayList<Texture> previewLayers;
  private Sound pop;
  private Sound popDown;
  private Sound settingsSound;
  private Sound errorSound;

  private int level = 0;
  private String fontString = "01";

  Vector3 touchPoint;


  public MenuState(StateManager stateManager) {
    super(stateManager);
    settingsSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/pickup.ogg"));
    errorSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/error.ogg"));
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts"
        + "/Ubuntu-MediumItalic.ttf"));
    FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("Fonts"
        + "/UbuntuMono-B.ttf"));
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = 80;
    parameter.color = new Color(.367f, .82f, .589f, 1f);
    font = generator.generateFont(parameter);
    parameter.size = 40;
    smallFont = generator.generateFont(parameter);// font size 12 pixels
    parameter.size = 20;
    parameter.color = new Color(.73f, .73f, .73f, 1f);
    leaderboardFont = generator2.generateFont(parameter);// font size 12 pixels
    generator.dispose(); // don't forget to dispose to avoid memory leaks!
    pop = Gdx.audio.newSound(Gdx.files.internal("Sounds/pop.ogg"));
    popDown = Gdx.audio.newSound(Gdx.files.internal("Sounds/popDown.ogg"));
    previewLayers = new ArrayList<>();
    ghostLayers = new ArrayList<>();
    previewBorder = new Texture("Images/Previews/p_border.png");
    setPreviewLayers();
    touchPoint = new Vector3();
    background = new Texture("Images/backgroundWithTitle.png");
    backButton = new Texture("Images/backButton.png");
    gold_trophy = new Texture("Images/gold_trophy.png");
    silver_trophy = new Texture("Images/silver_trophy.png");
    bronze_trophy = new Texture("Images/bronze_trophy.png");
    black_trophy = new Texture("Images/black_trophy.png");
    arrowRight = new Texture("Images/arrowRight.png");
    arrowRightGray = new Texture("Images/arrowRightGray.png");
    arrowLeft = new Texture("Images/arrowLeft.png");
    arrowLeftGray = new Texture("Images/arrowLeftGray.png");
    fish = new Texture("Images/fish.png");
    egg = new Texture("Images/egg_small.png");
    StarManager.checkScore(level);
    currentGhostLayer = 0;
    ghostLayers.add(new Texture("Images/Previews/ghost1.png"));
    ghostLayers.add(new Texture("Images/Previews/ghost2.png"));
    ghostLayers.add(new Texture("Images/Previews/ghost3.png"));
    ghostLayers.add(new Texture("Images/Previews/ghost4.png"));
    ghostLayers.add(new Texture("Images/Previews/ghost5.png"));
    ghostLayers.add(new Texture("Images/Previews/ghost6.png"));
  }

  @Override
  public void handleInput() {
    if (Gdx.input.justTouched()) {
      FlippyFish.camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
      int x = (int) touchPoint.x;
      int y = (int) touchPoint.y;

      if (x < 144 && y > 686 && FlippyFish.runningTime > 1_000_000_000) {
        // Back button pressed ** ANDROID ONLY
        FlippyFish.staticDispose();
        Gdx.app.exit();
        System.exit(0);
        return;
      }

      if (x >= 30 && x <= previewBorder.getWidth() + 30
          && y >= 250 && y <= previewBorder.getHeight() + 250) {
        launch();
      } else if (x >= 22 && x <= 150 && y >= 117 && y <= 245) {
        // Left button clicked
        if (level > 0) {
          if (FlippyFish.sfxOn) {
            popDown.play();
          }
        } else {
          if (FlippyFish.sfxOn) {
            errorSound.play();
            // Min level reached
          }
        }
        decrementLevel();
      } else if (x >= 326 && x <= 454 && y >= 117 && y <= 245) {
        // Right button clicked
        if (StarManager.checkScore(level)) {
          incrementLevel();
          if (FlippyFish.sfxOn) {
            pop.play();
          }
        } else {
          if (FlippyFish.sfxOn) {
            errorSound.play();
            // Max level reached
          }
        }
      } else if (x >= 404 && y >= 728) {
        // SettingsState icon clicked
        if (FlippyFish.sfxOn) {
          settingsSound.play(.7f);
        }
        stateManager.setState(2);
      } else if (FlippyFish.isSurvivalMode && x >= 240 && y <= 100) {
        // Get users name
//        Gdx.input.setOnscreenKeyboardVisible(true);
        MyTextInputListener listener = new MyTextInputListener();
        Gdx.input.getTextInput(listener, "Enter your name", "", "CLICK HERE");
      }
    }
  }

  @Override
  protected void start() {
    if(level == 0) {
      FlippyFish.isSurvivalMode = true;
      fontString = "Survival";
    }
    else if (level < 10) {
      fontString = "0" + level;
    } else {
      fontString = "" + level;
    }
    StarManager.checkScore(level);
    if (level == 0) {
      try {
        URL giverly = new URL("http://www.giverly.org/FlippyFish_GetScores.php");
        URLConnection yc = giverly.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        String inputLine = in.readLine();
        in.close();
        if (inputLine != null){
          String[] words = inputLine.trim().split("\\s+");
          FlippyFish.goldName = words[0];
          FlippyFish.goldScore = Integer.parseInt(words[1]);
          FlippyFish.silverName = words[2];
          FlippyFish.silverScore = Integer.parseInt(words[3]);
          FlippyFish.bronzeName = words[4];
          FlippyFish.bronzeScore = Integer.parseInt(words[5]);
        }
      } catch (IOException e) {
        FlippyFish.goldName = "Champion";
        FlippyFish.goldScore = 50;
        FlippyFish.silverName = "Runner Up";
        FlippyFish.silverScore = 25;
        FlippyFish.bronzeName = "Third Placer";
        FlippyFish.bronzeScore = 10;
      }
    }
    FlippyFish.runningTime = 0;
    StarManager.checkScore(level);
    FlippyFish.gameStartTime = TimeUtils.nanoTime();
    FlippyFish.getSurvivorScore();
  }

  @Override
  protected void update(float dt) {

  }

  private void launch() {
    FlippyFish.setLevel(level);
    stateManager.setState(1);
  }

  @Override
  public void render(SpriteBatch sb) {
    sb.begin();
    sb.draw(background, 0, 0, FlippyFish.WIDTH, FlippyFish.HEIGHT);

//     THIS IS FOR ANDROID ONLY
    if (FlippyFish.runningTime > 1_000_000_000) {
      sb.draw(backButton, 8, 736, 64, 64);
    }
    sb.draw(fish, 16 , 600);

    sb.draw(ghostLayers.get(currentGhostLayer), 30, 250);
    for(int i = 1; i < level && i < previewLayers.size(); i++) {
      sb.draw(previewLayers.get(i), 30, 250);
    }
    cycleGhostLayers();
    sb.draw(previewBorder, 30, 250);

    if (level == 0) {
      sb.draw(arrowLeftGray, 22, 116);
      smallFont.draw(sb, fontString, 164, 192);
      sb.draw(gold_trophy, 48, 96);
      sb.draw(silver_trophy, 272, 96);
      sb.draw(bronze_trophy, 48, 32);
      sb.draw(black_trophy, 272, 32);
      sb.draw(egg, 90, 90);
      sb.draw(egg, 90, 26);
      sb.draw(egg, 314, 90);
      sb.draw(egg, 314, 26);
      leaderboardFont.draw(sb, FlippyFish.goldName, 80, 124);
      leaderboardFont.draw(sb, "" + FlippyFish.goldScore, 112, 104);
      leaderboardFont.draw(sb, FlippyFish.silverName, 304, 124);
      leaderboardFont.draw(sb, "" + FlippyFish.silverScore, 336, 104);
      leaderboardFont.draw(sb, FlippyFish.bronzeName, 80, 60);
      leaderboardFont.draw(sb, "" + FlippyFish.bronzeScore, 112, 40);
      leaderboardFont.draw(sb, FlippyFish.playerName, 304, 60);
      leaderboardFont.draw(sb, "" + FlippyFish.highScore, 336, 40);
    } else {
      sb.draw(arrowLeft, 22, 116);
      font.draw(sb, fontString, 200, 206);
      sb.draw(StarManager.getTexture(), 115, 40);
    }
    if (StarManager.checkScore(level)) {
      sb.draw(arrowRight, 326, 116);
    } else {
      sb.draw(arrowRightGray, 326, 116);
    }
    sb.end();
  }


  private void setPreviewLayers() {
    previewLayers.add(new Texture("Images/Previews/p_01.png"));
    previewLayers.add(new Texture("Images/Previews/p_02.png"));
    previewLayers.add(new Texture("Images/Previews/p_03.png"));
    previewLayers.add(new Texture("Images/Previews/p_04.png"));
    previewLayers.add(new Texture("Images/Previews/p_05.png"));
    previewLayers.add(new Texture("Images/Previews/p_06.png"));
    previewLayers.add(new Texture("Images/Previews/p_07.png"));
    previewLayers.add(new Texture("Images/Previews/p_08.png"));
    previewLayers.add(new Texture("Images/Previews/p_09.png"));
    previewLayers.add(new Texture("Images/Previews/p_10.png"));
    previewLayers.add(new Texture("Images/Previews/p_11.png"));
    previewLayers.add(new Texture("Images/Previews/p_12.png"));
    previewLayers.add(new Texture("Images/Previews/p_13.png"));
    previewLayers.add(new Texture("Images/Previews/p_14.png"));
    previewLayers.add(new Texture("Images/Previews/p_15.png"));
    previewLayers.add(new Texture("Images/Previews/p_16.png"));
    previewLayers.add(new Texture("Images/Previews/p_17.png"));
    previewLayers.add(new Texture("Images/Previews/p_18.png"));
    previewLayers.add(new Texture("Images/Previews/p_19.png"));
    previewLayers.add(new Texture("Images/Previews/p_20.png"));
    previewLayers.add(new Texture("Images/Previews/p_21.png"));
    previewLayers.add(new Texture("Images/Previews/p_22.png"));
    previewLayers.add(new Texture("Images/Previews/p_23.png"));
    previewLayers.add(new Texture("Images/Previews/p_24.png"));
    previewLayers.add(new Texture("Images/Previews/p_25.png"));
  }

  @Override
  public void dispose() {
    background.dispose();
    fish.dispose();
    backButton.dispose();
    previewBorder.dispose();
    font.dispose();
    pop.dispose();
    popDown.dispose();
    settingsSound.dispose();
  }

  private void incrementLevel() {
    FlippyFish.isSurvivalMode = false;
    if(level >= 25) {
      return;
    } else {
      level++;
      StarManager.checkScore(level);
      if (level < 10){
        fontString = "0" + level;
      } else {
        fontString = "" + level;
      }
    }
  }

  private void decrementLevel() {
    if (level == 0) {
      return;
    }
    level--;
    if(level == 0) {
      FlippyFish.isSurvivalMode = true;
      fontString = "Survival";
    }
    else if (level < 10) {
      fontString = "0" + level;
    } else {
      fontString = "" + level;
    }
    StarManager.checkScore(level);
  }

  public void cycleGhostLayers() {
    if (ghostCycle < 0) {
      currentGhostLayer = (currentGhostLayer == 5) ? 0 : ++currentGhostLayer;
      ghostCycle = .3f;
      if (currentGhostLayer == 5) {
        ghostCycle = .6f;
      }
    } else {
      ghostCycle -= Gdx.graphics.getDeltaTime();
    }
  }
}
