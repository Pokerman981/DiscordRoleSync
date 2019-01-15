package me.pokerman99.DiscordRoleSync;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

@Plugin(id = "discordrolesync", name = "DiscordRoleSync", version = "1.0", dependencies = {
		@Dependency(id = "luckperms", optional = false)
	})
public class Main{
	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = false)
	public ConfigurationLoader<CommentedConfigurationNode> loader;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path ConfigDir;
	
	@Inject
	public PluginContainer plugin;
	public PluginContainer getPlugin() {
		return this.plugin;
	}

	public static CommentedConfigurationNode rootNode;

	public static CommentedConfigurationNode config() {
		return rootNode;
	}

	public void save() throws IOException {
		loader.save(rootNode);
	}

	@Inject
	private Logger logger;

	public Logger getLogger() {
		return logger;
	}
	
    public static Main instance;
    
    public static Main getInstance() {
        return instance;
    }
    

	@Listener
	public void onPreInit(GameStartingServerEvent event) {
		String server = Sponge.getGame().getServer().getMotd().toString().replace("{", "")
				.replace("Text", "").replace("}", "");
		discord.Login(server);
		//Sponge.getEventManager().registerListeners(this, new serverInfoCommand());
		Sponge.getEventManager().registerListeners(this, new roleSync(this));
		try {rootNode = loader.load();} catch (IOException e1) {e1.printStackTrace();}
		config().getNode("config-version").setValue("1.1");
		try {loader.save(config());} catch (IOException e) {e.printStackTrace();}
		CommandSpec link = CommandSpec.builder().executor(new roleSync(this)).build();
		Sponge.getCommandManager().register(this, link, Lists.newArrayList("link"));
		if (config().getNode("pins").isVirtual()){
		List<String> temp = new ArrayList<String>();
		temp.add("UbyZZQB7pmaCAv9w");
		config().getNode("pins").setValue(temp);
		try {loader.save(config());} catch (IOException e) {e.printStackTrace();}
		}
		if (config().getNode("linked").isVirtual()){
		List<String> temp = new ArrayList<String>();
		temp.add("pokerman99");
		config().getNode("linked").setValue(temp);
		try {loader.save(config());} catch (IOException e) {e.printStackTrace();}
		}

		CommandSpec checkPin = CommandSpec.builder().permission("discord.checkpin").arguments(GenericArguments.string(Text.of("name"))).executor((src, args) -> {
				if (!args.getOne(Text.of("name")).isPresent()) {
					roleSync.sendMessage(src, "&cYou must supply a user name");
					return CommandResult.empty();
				}

				String name =  args.getOne(Text.of("name")).get().toString();
				Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);

				if (!userStorage.get().get(name).isPresent()) {
				roleSync.sendMessage(src, "&cUnable to find this player. Are they real?");
				return CommandResult.empty();
			}
				String uuid = userStorage.get().get(name).get().getIdentifier();
				String pin = rootNode.getNode("records", uuid, "pin").getString();

				roleSync.sendMessage(src, "&a" + name + "'s pin is " + pin);
			return  CommandResult.success();
		}).build();

		Sponge.getCommandManager().register(getInstance(), checkPin, "checkping");

	}
	
	@Listener
	public void onShutDown(GameStoppingEvent event){
		discord.Logout();
	}
	
}
