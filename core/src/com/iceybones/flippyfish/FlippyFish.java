package com.iceybones.flippyfish;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class FlippyFish extends ApplicationAdapter {

	public static final String title = "Flippy Fish";
	public static String playerName = "CLICK HERE";
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static int level = 1;
	public static final long START_TIME_OFFSET = 1_400_000_000;
	public static final long DOUBLE_CLICK_SPEED = 100_000_000;
	public static Preferences scores;
	public static Preferences ghostJumps;
	public static int highScore;
	public static int roundScore;
	public static int highScoreSinceCompleted = -1;
	public static String goldName = "";
	public static int goldScore = 0;
	public static String silverName = "";
	public static int silverScore = 0;
	public static String bronzeName = "";
	public static int bronzeScore = 0;
	public static long oilSpillSeed;
	public static long boatSeed;
	public static long pickupSeed;
	public static long seedBase = 3302211476L;
	public static long gameStartTime = 0;
	public static long runningTime = 0;
	public static boolean soundOn = true;
	public static boolean sfxOn = true;
	public static boolean isSurvivalMode = false;
	private static boolean gamePaused;
	private static long totalPausedTime = 0;
	private static long pausedStartTime = 0;


	public static SpriteBatch batch;
	public static OrthographicCamera camera;
	private static StateManager stateManager;
	
	@Override
	public void create () {
		scores = Gdx.app.getPreferences("scores");
		ghostJumps = Gdx.app.getPreferences("ghostJumps");
		scores.flush();
		if (scores.contains("name")) {
			playerName = scores.getString("name");
		}

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		batch = new SpriteBatch();
		Gdx.gl.glClearColor(22, 22, 22, 1);
		stateManager = new StateManager();
	}

	@Override
	public void render () {
		if (gamePaused) {
			return;
		}
		runningTime = TimeUtils.nanoTime() - gameStartTime;
		stateManager.handleInput();
		stateManager.update(Gdx.graphics.getDeltaTime());
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stateManager.render(batch);
	}

	@Override
	public void dispose () {
		batch.dispose();
		stateManager.dispose();
	}

	public static void staticDispose() {
		batch.dispose();
		stateManager.dispose();
	}



	@Override
	public void pause() {
		pausedStartTime = TimeUtils.nanoTime();
		gamePaused = true;
		if (stateManager.getActiveState() == stateManager.getStates().get(1)) {
			((PlayState) stateManager.getStates().get(1)).getGhostFish().getGhost().saveGhost();
			saveScore();
		}
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
		gamePaused = false;
		gameStartTime += (TimeUtils.nanoTime() - pausedStartTime);
		pausedStartTime = 0;
	}

	public static OrthographicCamera getCamera() {
		return camera;
	}

	public static void saveScore() {
		scores.putInteger(FlippyFish.level + ":h", highScore);
		scores.flush();
		if (!scores.contains("name") || scores.getString("name").trim().isEmpty()) {
			scores.putString("name", playerName);
		}
		try {
			URL giverly = new URL("http://www.giverly.org/FlippyFish_PostScore.php?name=" + scores.getString("name") + "&score=" + highScore);
			URLConnection yc = giverly.openConnection();
//			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//			String inputLine;
//			while ((inputLine = in.readLine()) != null)
//				System.out.println(inputLine);
//			in.close();
		} catch (IOException e) {
//			throw new RuntimeException(e);
		}
	}

	public static void setLevelCompleted() {
		scores.putInteger(FlippyFish.level + ":c", 0);
		highScoreSinceCompleted = 0;
		scores.flush();
	}

	public static boolean checkLevelCompleted() {
		if(scores.contains(FlippyFish.level + ":c")) {
			return  true;
		} else {
			return false;
		}
	}

	public static void saveHighScoreSinceCompleted(int score) {
		scores.putInteger(FlippyFish.level + ":c", score);
		highScoreSinceCompleted = score;
	}

	public static int getHighScoreSinceCompleted() {
		if(scores.contains(FlippyFish.level + ":c")) {
			highScoreSinceCompleted = scores.getInteger(FlippyFish.level + ":c");
			return  highScoreSinceCompleted;
		} else {
			return -1;
		}
	}


	public static void loadScore() {
  	if(scores.contains(FlippyFish.level + ":h")) {
			highScore = scores.getInteger(FlippyFish.level + ":h");
		} else {
  		highScore = 0;
		}
	}

	public static void getSurvivorScore() {
		if(scores.contains("0:h")) {
			highScore = scores.getInteger("0:h");
		} else {
			highScore = 0;
		}
	}

	public static void setLevel(int lvl) {
		level = lvl;
		oilSpillSeed = (lvl + 1) * seedBase;
		boatSeed = (lvl + 1) * seedBase;
		pickupSeed = (lvl + 1) * seedBase;
		if (level == 0) {
			isSurvivalMode = true;
		}
	}

	public static int getLevel() {
		return level;
//		return 20;
	}

	public static StateManager getStateManager() {
		return stateManager;
	}
}
