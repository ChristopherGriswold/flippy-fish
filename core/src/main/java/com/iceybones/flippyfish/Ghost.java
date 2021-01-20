package com.iceybones.flippyfish;

import com.badlogic.gdx.utils.Array;

public class Ghost {
//  private ArrayList<Jump> jumps;
  private Array<Jump> jumps;
  private long deathTimeStamp;
  private int jumpIndex;

  public Ghost() {
    jumps = new Array<>();
    deathTimeStamp = 0;
    jumpIndex = 0;
  }

  public void cloneGhost(Ghost specimen) {
    jumps.clear();
    this.deathTimeStamp = specimen.deathTimeStamp;
    jumpIndex = 0;
    for (int i = 0; i < specimen.jumps.size; i++) {
      this.jumps.add(new Jump(specimen.jumps.get(i).time, specimen.jumps.get(i).posY,
          specimen.jumps.get(i).velY));
    }
  }

  public void addJump(long time, float posY, float velY) {
    jumps.add(new Jump(time, posY, velY));
  }

  public void addJump(Jump jump) {
    jumps.add(jump);
  }

  public Jump getNextJump() {
    if (jumps.size > jumpIndex) {
      return jumps.get(jumpIndex++);
    } else {
      return null;
    }
  }

  public void saveGhost() {
    int jumpSize = jumps.size;
    for (int i = 0; i < jumpSize; i++) {
      // Build string for each jump and save with i = id;
      String entryString = jumps.get(i).time + "," + jumps.get(i).posY + "," + jumps.get(i).velY;
      FlippyFish.ghostJumps.putString(FlippyFish.getLevel() + ":" + i, entryString);
    }
    while (FlippyFish.ghostJumps.contains(FlippyFish.getLevel() + ":" + jumpSize)){
      FlippyFish.ghostJumps.remove(FlippyFish.getLevel() + ":" + jumpSize);
      jumpSize++;
    }
    if (deathTimeStamp != 0) {
      FlippyFish.ghostJumps.putLong(FlippyFish.getLevel() + ":dt",
          deathTimeStamp);
    } else if (FlippyFish.ghostJumps.contains(FlippyFish.getLevel() + ":dt")){
      FlippyFish.ghostJumps.remove(FlippyFish.getLevel() + ":dt");
    }
    FlippyFish.ghostJumps.flush();
  }


  public void loadGhost() {
    int id = 0;
    long time;
    float posY;
    float velY;
    int firstComma;
    int secondComma;
    String entry;
    if (FlippyFish.ghostJumps.contains(FlippyFish.getLevel() + ":dt")) {
      deathTimeStamp = FlippyFish.ghostJumps.getLong(FlippyFish.getLevel() + ":dt");
    }
    while (FlippyFish.ghostJumps.contains(FlippyFish.getLevel() + ":" + id)) {
      entry = FlippyFish.ghostJumps.getString(FlippyFish.getLevel() + ":" + id);
      firstComma = entry.indexOf(',');
      secondComma = entry.indexOf(',', firstComma + 1);
      time = Long.parseLong(entry.substring(0, firstComma));
      posY = Float.parseFloat(entry.substring(firstComma + 1, secondComma));
      velY = Float.parseFloat(entry.substring(secondComma + 1));
      jumps.add(new Jump(time, posY, velY));
      id++;
    }
  }

  public long getDeathTimeStamp() {
    return deathTimeStamp;
  }

  public void setDeathTimeStamp(long time) {
    this.deathTimeStamp = time;
  }

  public void clearGhost() {
    jumps.clear();
    deathTimeStamp = 0;
    resetJumpIndex();
  }

  public void resetJumpIndex() {
    jumpIndex = 0;
  }

  public int jumpListSize() {
    return jumps.size;
  }

  public class Jump {
    private long time;
    private float posY;
    private float velY;

    public Jump(long time, float posY, float velY) {
      this.time = time;
      this.posY = posY;
      this.velY = velY;
    }

    public long getTime() {
      return time;
    }

    public float getPosY() {
      return posY;
    }

    public float getVelY() {
      return velY;
    }
  }
}
