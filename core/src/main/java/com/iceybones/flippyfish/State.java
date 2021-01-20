package com.iceybones.flippyfish;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class State implements Disposable {
  protected StateManager stateManager;

  protected State(StateManager stateManager) {
    this.stateManager = stateManager;
  }

  protected abstract void handleInput();
  protected abstract void start();
  protected abstract void update(float dt);
  protected abstract void render(SpriteBatch sb);
//  protected abstract void dispose();
}
