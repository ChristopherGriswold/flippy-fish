package com.iceybones.flippyfish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Wave extends GameObject{

  public Wave(int x, int y) {
    super(x, y);
    texture = new Texture(Gdx.files.internal("Images/wave.png"));
  }

  public void update(float dt) {
    if (position.x <= -960) {
      position.x = 0;
    }
    position.x -= 200 * dt;
  }
}