package app.jptech.devtoolkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class IdWand implements Listener {
	static Map<String, Material> playerList = new HashMap<String, Material>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Material matType;
		Player player;
		
		if (event.getPlayer().hasPermission("devtoolkit.idwand")) {
			// IdWand interact event
			player = event.getPlayer();
			if (player.hasPermission("devtoolkit.idwand")) {
				Block block = event.getClickedBlock();
				matType = playerList.get(player.getName());
				if (block != null && matType != null) {
					if (player.getInventory().getItemInMainHand().getType() == matType && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {
						player.sendMessage(block.getType().toString());
					}
				}
			}
		}
	}
}
