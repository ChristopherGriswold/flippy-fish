package com.iceybones.flippyfish;

import com.badlogic.gdx.Input.TextInputListener;

public class MyTextInputListener implements TextInputListener {
  @Override
  public void input (String text) {
    if (text.length() > 12) {
      text = text.substring(0, 12);
    }
    if (text.trim().isEmpty()){
      text = "Anonymous";
    }
    FlippyFish.playerName = text;
    FlippyFish.scores.putString("name", text);
    FlippyFish.scores.putInteger("0:h", 0);
    FlippyFish.scores.flush();
    FlippyFish.highScore = 0;
  }

  @Override
  public void canceled () {
  }
}