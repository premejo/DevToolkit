package app.jptech.devtoolkit;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Sound;

class SoundPlayer {
	static ArrayList<SoundPlayer> soundPlayers = new ArrayList<SoundPlayer>();
	static ArrayList<Sound> allSounds = new ArrayList<Sound>();
	
	int sound = 0;
	float volume = 1.0f;
	int pitchSet = 0;
	int delay = 1000;
	int menu = 0; // 0=sound, 1= volume, 2=pitch, 3=delay
	Location location;
	boolean isPlaying = false;
	
	SoundPlayer(Location location2) {
		location = location2;
	}
	
	void playCurrentSound() {
		location.getWorld().playSound(location, allSounds.get(sound), volume, getPitchVal());
	}
	
	float getPitchVal() {
		float fraction = (float) (pitchSet/12.0);
		return (float) (0.5 * Math.pow(2, (fraction)));
	}
}
