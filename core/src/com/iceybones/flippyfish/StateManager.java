package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;

public class StateManager implements Disposable {
  private final ArrayList<State> states;
  private State activeState;

  public StateManager() {
    SplashScreen splashScreen = new SplashScreen(this);
    splashScreen.start();
    FlippyFish.scores = Gdx.app.getPreferences("scores");
    FlippyFish.ghostJumps = Gdx.app.getPreferences("ghostJumps");
    activeState = splashScreen;
    states = new ArrayList<>();
    states.add(new MenuState(this));
    states.add(new PlayState(this));
    states.add(new SettingsState(this));
    states.add(splashScreen);
    setState(3);
   // activeState = states.get(0);
  }

  public void setState(int index) {
    states.get(index).start();
    activeState = states.get(index);
  }
  public void handleInput() {
    activeState.handleInput();
  }
  public void update(float dt) {
    activeState.update(dt);
  }

  public void render(SpriteBatch batch) {
    activeState.render(batch);
  }

  public ArrayList<State> getStates() {
    return states;
  }

  public State getActiveState() {
    return activeState;
  }

  @Override
  public void dispose() {
    for (State state : states) {
      state.dispose();
    }
  }
}
