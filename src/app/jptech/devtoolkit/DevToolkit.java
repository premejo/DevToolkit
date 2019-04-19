package app.jptech.devtoolkit;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DevToolkit extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		FileConfiguration config = getConfig();
		
		config.addDefault("resetTimer", 1);
		
		config.options().copyDefaults(true);
		saveConfig();

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new app.jptech.devtoolkit.IdWand(), this);
		
		// register commands
		this.getCommand("smitefoe").setExecutor(new OnPlayerCMD());
		this.getCommand("idwand").setExecutor(new OnPlayerCMD());
		this.getCommand("soundtest").setExecutor(new OnPlayerCMD());
		
		 // load all sounds into allSounds
		for (Sound sound: Sound.values()) {
			if (!sound.toString().contains("MUSIC") && !sound.toString().contains("RECORD")) SoundPlayer.allSounds.add(sound);
		}
	}
	
	@Override
	public void onDisable() {
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		// stop playing and remove SoundPlayer instance when block broken
		for (BlockFace face: BlockFace.values()) {
			if (event.getBlock().getRelative(face).getType() == Material.WALL_SIGN) {
				for (SoundPlayer sp:SoundPlayer.soundPlayers) {
					if (event.getBlock().getRelative(face).getLocation().equals(sp.location)) {
						sp.isPlaying = false;
						SoundPlayer.soundPlayers.remove(sp);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent event) {
		
		// play sound from block if redstone current applied
		for (BlockFace face: BlockFace.values()) {
			if (event.getBlock().getRelative(face).getType() == Material.WALL_SIGN) {
				for (SoundPlayer sp:SoundPlayer.soundPlayers) {
					if (event.getBlock().getRelative(face).getLocation().equals(sp.location)) {
						sp.playCurrentSound();
						break;
					}
				}
			}
		}
	}

	  @EventHandler
	  public void onPlayerInteract(PlayerInteractEvent event) {
		  
		  if (event.getPlayer().hasPermission("devtoolkit.soundtest")) {
	
			  // handle event with clicking on SoundPlayer signs
			  if(event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.WALL_SIGN) {
				  Sign sign = (Sign)event.getClickedBlock().getState();
				  String sign0 = sign.getLine(0).trim();
				  if (sign0.equalsIgnoreCase("SoundTest")) {
					  SoundPlayer foundSoundPlayer = null;
					  String sign1 = sign.getLine(1).trim();
					  
					  // select sign for use if existing SoundPlayer
					  for (SoundPlayer sp:SoundPlayer.soundPlayers) {
						  if (sp.location.equals(sign.getLocation())) foundSoundPlayer = sp;
					  }
					  
					  // otherwise create new SoundPlayer
					  if (foundSoundPlayer == null) {
						  SoundPlayer spnew = new SoundPlayer(sign.getLocation());
						  if (sign1.length() == 0) {
							  try {
								  spnew.sound = Integer.parseInt(sign1);
							  } catch (NumberFormatException nfe) {
								  spnew.sound = 0;
							  }
							  if (spnew.sound > SoundPlayer.allSounds.size()-1) spnew.sound = 0;
						  }
						  SoundPlayer.soundPlayers.add(spnew);
						  String sign3 = SoundPlayer.allSounds.get(spnew.sound).toString();
						  sign.setLine(1, "" + spnew.sound);
						  sign.setLine(2, "Sound");
						  sign.setLine(3, sign3);
						  sign.update();
					  } else {
						  if (event.getAction() == Action.LEFT_CLICK_BLOCK) { // left click - change value
							  if (foundSoundPlayer.menu == 0) { // Sound
								  if (event.getPlayer().isSneaking()) {
									  foundSoundPlayer.sound--;
									  if( foundSoundPlayer.sound < 0 ) {
										  foundSoundPlayer.sound = SoundPlayer.allSounds.size()-1;
									  }
								  } else {
									  foundSoundPlayer.sound++;
									  if (foundSoundPlayer.sound >= SoundPlayer.allSounds.size()) {
										  foundSoundPlayer.sound = 0;
									  }
								  }
		
								  String sign3 = SoundPlayer.allSounds.get(foundSoundPlayer.sound).toString();
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(2, "Sound");
								  sign.setLine(3, sign3);
								  sign.update();
								  foundSoundPlayer.playCurrentSound();
							  } else if (foundSoundPlayer.menu == 1) { // Volume
								  if( event.getPlayer().isSneaking() )
								  {
									  foundSoundPlayer.volume -= 0.1f;
									  if (foundSoundPlayer.volume < 0)
										  foundSoundPlayer.volume = 0;
								  } else
								  {
									  foundSoundPlayer.volume += 0.1f;
									  if (foundSoundPlayer.volume > 1)
										  foundSoundPlayer.volume = 1;
								  }
								  String sign3 =  "" + foundSoundPlayer.volume;
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(3, sign3);
								  sign.update();
								  foundSoundPlayer.playCurrentSound();
							  } else if (foundSoundPlayer.menu == 2) { // Pitch
								  if (event.getPlayer().isSneaking() )
								  {
									  foundSoundPlayer.pitchSet -= 1;
									  if (foundSoundPlayer.pitchSet < 0)
										  foundSoundPlayer.pitchSet = 0;
								  } else
								  {
									  foundSoundPlayer.pitchSet += 1;
									  if (foundSoundPlayer.pitchSet > 24)
										  foundSoundPlayer.pitchSet = 24;
								  }
								  String sign3 = foundSoundPlayer.pitchSet + " (" + foundSoundPlayer.getPitchVal() + ")";
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(3, sign3);
								  sign.update();
								  foundSoundPlayer.playCurrentSound();
							  } else if (foundSoundPlayer.menu == 3) { // Delay
								  if ( event.getPlayer().isSneaking() )
								  {
									  foundSoundPlayer.delay -= 100;
									  if (foundSoundPlayer.delay < 100)
										  foundSoundPlayer.delay = 100;
								  } else
								  {
									  foundSoundPlayer.delay += 100;
								  }
								  String sign3 =  "" + foundSoundPlayer.delay;
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(3, sign3);
								  sign.update();
								  foundSoundPlayer.playCurrentSound();
							  }	  
					  } else // right click - change menu
						  {
							  if (foundSoundPlayer.menu == 0) {
								  foundSoundPlayer.menu = 1;
								  String sign3 = "" + foundSoundPlayer.volume;
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(2, "Volume");
								  sign.setLine(3, sign3);
								  sign.update();
							  } else if (foundSoundPlayer.menu == 1) {
								  foundSoundPlayer.menu = 2;
								  String sign3 = foundSoundPlayer.pitchSet + " (" + foundSoundPlayer.getPitchVal() + ")";
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(2, "Pitch");
								  sign.setLine(3, sign3);
								  sign.update();
							  } else if (foundSoundPlayer.menu == 2) {
								  foundSoundPlayer.menu = 3;
								  String sign3 = "" + foundSoundPlayer.delay;
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(2, "Delay");
								  sign.setLine(3, sign3);
								  sign.update();
							  } else if (foundSoundPlayer.menu == 3) {
								  foundSoundPlayer.menu = 0;
								  String sign3 = SoundPlayer.allSounds.get(foundSoundPlayer.sound).toString();
								  sign.setLine(1, "" + foundSoundPlayer.sound);
								  sign.setLine(2, "Sound");
								  sign.setLine(3, sign3);
								  sign.update();
							  }
						  }
					  }
					  event.setCancelled(true); // will complete above actions and stop (ex: block will not break)
				  }
			  } else if (event.getClickedBlock() != null && (event.getClickedBlock().getType().toString().contains("BUTTON"))) {
				  if (event.getClickedBlock().getRelative(BlockFace.UP).getType() == Material.WALL_SIGN) { // sign
					  Sign sign = (Sign)event.getClickedBlock().getRelative(BlockFace.UP).getState();
					  String sign0 = sign.getLine(0).trim();
					  if (sign0.equalsIgnoreCase("SoundTest")) {
						  for (SoundPlayer sp:SoundPlayer.soundPlayers){
							  if (sp.location.equals(sign.getLocation())) sp.playCurrentSound();
						  }  
					  }
				  }
			  } else if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.LEVER)) { // lever
				  if( event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.WALL_SIGN ) { // sign
					  Sign sign = (Sign)event.getClickedBlock().getRelative(BlockFace.DOWN).getState();
					  String sign0 = sign.getLine(0).trim();
					  if (sign0.equalsIgnoreCase("SoundTest")) {
						  for (SoundPlayer sp:SoundPlayer.soundPlayers){
							  if (sp.location.equals(sign.getLocation())) {
								  if (sp.isPlaying) sp.isPlaying = false;
								  else {
									  sp.isPlaying = true;
									  soundThread(sp);
								  }
							  }
						  }
					  }
				  }
			  }
		  }
	  }
	  
	private void soundThread(final SoundPlayer sp){
		new Thread(){
				
	  	@Override
		public void run() {
	  		
	  		setPriority(MIN_PRIORITY);
			//taskNum = -1;
			try{
				while( sp.isPlaying)
				{
						sleep(sp.delay);
						soundThreadUpdate(sp);
				} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	  	}.start();
	}

	private void soundThreadUpdate(final SoundPlayer sp) {
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
	
			public void run() {
				// plays sound
				sp.playCurrentSound();
			}
	  	});
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
	    Player player = event.getPlayer();
	    
	    player.sendMessage("You have not yet cast lightning upon a foe!  \nusage: /smitefoe <player>(optional, random w/o)");
	}
}
	