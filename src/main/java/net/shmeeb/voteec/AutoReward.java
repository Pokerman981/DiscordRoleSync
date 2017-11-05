package net.shmeeb.voteec;

import java.io.IOException;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class AutoReward {
	
	public Main plugin;

	public AutoReward(Main pluginInstance) {
		this.plugin = pluginInstance;
	}

	@Listener
	public void AutoReward(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		String username = player.getName();
		String pid = player.getIdentifier();
		UUID playerID = UUID.fromString(pid);
		if (Main.userStorage.get().get(username).isPresent()) {
			playerID = Main.userStorage.get().get(username).get().getUniqueId();
			if (Main.storedVotes.containsKey(playerID)) {
				Main.storedVotes.put(playerID, Main.storedVotes.get(playerID) + 1);
			} else {
				plugin.storedVotes.put(playerID, 1);
			}

			try {
				plugin.saveOffline(); 
			} catch (IOException e) {
				plugin.getLogger().error("Couldn't save that offline player's vote!", e);
			}
		}
	}

}
