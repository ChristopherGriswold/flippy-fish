package com.iceybones.flippyfish.android;

import android.content.Context;
import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.iceybones.flippyfish.FlippyFish;
import com.iceybones.flippyfish.PlayState;
import java.io.File;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
	FlippyFish flippyFish = new FlippyFish();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
		configuration.useCompass = false;
		configuration.useAccelerometer = false;
		initialize(flippyFish, configuration);
	}

	@Override
	protected void onDestroy() {
		((PlayState) FlippyFish.getStateManager().getStates().get(1)).goToMenu();
		flippyFish.dispose();
		trimCache(getContext());
		super.onDestroy();

	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// Handle Exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
}