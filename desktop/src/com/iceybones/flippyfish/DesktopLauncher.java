package com.iceybones.flippyfish;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.iceybones.flippyfish.FlippyFish;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Flippy FIsh");
		config.setWindowedMode(480, 800);
		config.setWindowIcon("Images/libgdx128.png", "Images/libgdx64.png", "Images/libgdx32.png", "Images/libgdx16.png");
		new Lwjgl3Application(new FlippyFish(), config);
	}
}
