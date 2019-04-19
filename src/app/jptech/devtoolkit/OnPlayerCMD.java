package app.jptech.devtoolkit;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnPlayerCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Collection<? extends Player> playerList = Bukkit.getOnlinePlayers();
		ArrayList<String> playerNames = new ArrayList<String>();
		World playerWorld = ((Player) sender).getWorld();

		// populate playerList with all players but player sending command
		for (Player players : playerList){
			if (!players.getName().equals(sender.getName())) playerNames.add(players.getName());
		}

		short theChosen = (short) Math.round((Math.random()*(playerNames.size() - 1)));
		if (!playerNames.isEmpty()) playerWorld = Bukkit.getPlayer(playerNames.get(theChosen)).getWorld();

		if (command.getName().equalsIgnoreCase("smitefoe") && sender.hasPermission("devtoolkit.smitefoe")) {
			if (args.length == 1) {
				if (Bukkit.getPlayer(args[0]) != null) { // check if parameter matches online player name
					// code to send lightning bolt to player in player list
					playerWorld.strikeLightning(Bukkit.getPlayer(args[0]).getPlayer().getLocation());
					sender.sendMessage("Your rage is unleashed as you smite "+Bukkit.getPlayer(args[0]).getPlayerListName()+" with lightning!");
				}
				else {
					//show usage
					if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) return false;
					else sender.sendMessage("No player with the name "+args[0]+" was found.");
				}
			}
			else {
				// code to select random player unless alone
				if (!playerNames.isEmpty()) {
					playerWorld.strikeLightning(Bukkit.getPlayer(playerNames.get(theChosen)).getPlayer().getLocation());
					sender.sendMessage("Your rage can no longer be contained and you smite "+Bukkit.getPlayer(playerNames.get(theChosen)).getPlayerListName()+" with lightning!");
				}
				else sender.sendMessage("You ready yourself to cast lightning on a foe, but realize you are the only one here.");
			}
		}
		else if (command.getName().equalsIgnoreCase("idwand") && sender.hasPermission("devtoolkit.idwand")) {
			if (args.length != 0) {
				return false;
			} else {
				if (IdWand.playerList.get(sender.getName()) == ((Player) sender).getInventory().getItemInMainHand().getType()) {
					IdWand.playerList.put(sender.getName(), null);
					sender.sendMessage("IdWand material cleared.");
				}
				else {
					IdWand.playerList.put(sender.getName(), ((Player) sender).getInventory().getItemInMainHand().getType());
					sender.sendMessage("IdWand material set to: "+IdWand.playerList.get(sender.getName()));
				}
			}
		}
		else if (command.getName().equalsIgnoreCase("soundtest") && sender.hasPermission("devtoolkit.soundtest")) {
			if (args.length != 0) return false;
			else return false;
		}
		return true;
	}
}
