package me.pokerman99.DiscordRoleSync;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.inject.Inject;

import me.pokerman99.DiscordRoleSync.commands.serverInfoCommand;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "discordrolesync", name = "DiscordRoleSync", version = "1.0")
public class Main{
	@Inject
	private Game game;

	private Game getGame() {
		return this.game;
	}

	@Inject
	private PluginContainer plugin;

	private PluginContainer getPlugin() {
		return this.plugin;
	}

	@Inject
	public Logger logger;

	public Logger getLogger() {
		return logger;
	}

	@Inject
	@DefaultConfig(sharedRoot = false)
	public Path defaultConfig, offlineVotes;

	@Inject
	@DefaultConfig(sharedRoot = false)
	public ConfigurationLoader<CommentedConfigurationNode> loader;

	@Inject
	@ConfigDir(sharedRoot = false)
	public Path privateConfigDir;
	public CommentedConfigurationNode rootNode;
	
    public static Main instance;
    
    public static Main getInstance() {
        return instance;
    }

	@Listener
	public void onPreInit(GameInitializationEvent event) {
		discord.Main();
		Sponge.getEventManager().registerListeners(this, new serverInfoCommand());
		return;
	}
	

	public void onJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		//MessageChannel.TO_ALL.send(Text.of(discord.Main().getGuilds()));
		return;
	}

}
