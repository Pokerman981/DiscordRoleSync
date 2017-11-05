package net.shmeeb.voteec;

import com.google.inject.Inject;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Plugin(id = "voteec", name = "VoteEC", version = "1.0", dependencies = @Dependency(id = "nuvotifier", version = "1.0", optional = false))
public class Main {
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
    private Logger logger;
    public Logger getLogger() {
        return logger;
    }

    @Inject @DefaultConfig(sharedRoot = false)
    private Path defaultConfig, offlineVotes;

    @Inject @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;
    private CommentedConfigurationNode rootNode;
    
	public Calendar calendar() {
		Calendar cal = Calendar.getInstance();
		return cal;
	}

    ///////////////////////////////////////////////////////////////////////
    public static HashMap<UUID,Integer> storedVotes = new HashMap<>();
    public static Optional<UserStorageService> userStorage;

    private String url;
    private String single_broadcast_message;
    private String double_broadcast_message;
    private List<String> single_rewards;
    private List<String> double_rewards;
///////////////////////////////////////////////////////////////////////

    private void saveDefaultConfigs() {
        userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
        Asset configAsset = plugin.getAsset("voteec.conf").orElse(null);
        offlineVotes = Paths.get(privateConfigDir.toString(),"", "offlinevotes.dat");

        if (Files.notExists(defaultConfig)) {
            if (configAsset != null) {
                try {
                    getLogger().info("Copying default config...");
                    configAsset.copyToFile(defaultConfig);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                getLogger().error("Could not find the default config file in the jar! Did you open the jar and delete it?");
                return;
            }
        }

        if (Files.notExists(offlineVotes)) {
            try {
                saveOffline();
            } catch (IOException e) {
                getLogger().error("Could not initialize the offlinevotes file!");
            }
        }
    }

    private boolean reloadConfigs() {
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            getLogger().error("There was an error while reloading your configs", e.toString());
            return false;
        }

        url = rootNode.getNode("config","url").getString();
        single_broadcast_message = rootNode.getNode("config","single-broadcast-message").getString();
        double_broadcast_message = rootNode.getNode("config","double-broadcast-message").getString();
        single_rewards = rootNode.getNode("config","single-rewards").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        double_rewards = rootNode.getNode("config","double-rewards").getChildrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());

        getLogger().info("Trying to load offline player votes from " + offlineVotes.toString());

        try {
            loadOffline();
        } catch (IOException e) {
            getLogger().error("Couldn't load up the stored offline player votes",e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void registerCommands() {
        CommandSpec reload = CommandSpec.builder()
                .description(Text.of("Reloads configuration"))
                .permission("voteec.reload")
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        if (reloadConfigs()) {
                            src.sendMessage(Text.of("Reloaded successfully!"));
                        } else {
                            src.sendMessage(Text.of("Could not reload properly"));
                        }
                        return CommandResult.success();
                    }
                }).build();
        
        CommandSpec test = CommandSpec.builder()
                .description(Text.of("Test command"))
                .permission("voteec.test")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        Player player = args.<Player>getOne("player").get();
                        String username = player.getName();
                        String pid = player.getIdentifier();
                        UUID playerID = UUID.fromString(pid);
                        if(userStorage.get().get(username).isPresent()) {
                            playerID = userStorage.get().get(username).get().getUniqueId();
                            if(storedVotes.containsKey(playerID)) {
                                storedVotes.put(playerID, storedVotes.get(playerID) + 1);
                            } else {
                                storedVotes.put(playerID, 1);
                            }

                            try {
                                saveOffline();
                            } catch (IOException e) {
                                getLogger().error("Couldn't save that offline player's vote!", e);
                            }
                        }
                        MessageChannel.TO_ALL.send(Text.of(storedVotes.size()));
                        MessageChannel.TO_ALL.send(Text.of(storedVotes.toString()));
                        MessageChannel.TO_ALL.send(Text.of(storedVotes.get(playerID)));

                        return CommandResult.success();
                    }
                }).build();
        
        

        CommandSpec sendVote = CommandSpec.builder()
                .description(Text.of("Sends a vote as a player"))
                .permission("voteec.sendvote")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        Player player = args.<Player>getOne("player").get();

                        handleVote(player.getName());
                        src.sendMessage(Text.of("You have successfully given " + player.getName() + " a vote"));

                        return CommandResult.success();
                    }
                }).build();

        Sponge.getCommandManager().register(this, reload,"vreload","voteecreload");
        Sponge.getCommandManager().register(this, sendVote, "sendvote");
        Sponge.getCommandManager().register(this, test, "test");
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        saveDefaultConfigs();
        reloadConfigs();
        registerCommands();
        Sponge.getEventManager().registerListeners(this, new AutoReward(this));
    }

    @Listener
    public void onVote(VotifierEvent e) {
        Vote vote = e.getVote();
        String username = vote.getUsername();

        getLogger().info("Vote received from " + vote.getServiceName() + " for " + username);
        handleVote(username);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        UUID playerID = event.getTargetEntity().getUniqueId();
        String username = event.getTargetEntity().getName();

        if(storedVotes.containsKey(playerID)) {
            for(int i = 0; i < storedVotes.get(playerID); i++) {
                handleVote(username);
            }

            storedVotes.remove(playerID);
            try {
                saveOffline();
            } catch (IOException e) {
                getLogger().error("Error while saving offline votes file", e);
            }
        }
    }

    private void handleVote(String username) {
        if (getGame().getServer().getPlayer(username).isPresent()) {
            Player player = getGame().getServer().getPlayer(username).get();
            URL u = null;

            try {
                u = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (player.hasPermission("voteec.double")) {
                String temp = double_broadcast_message.replaceAll("%player%", username);
                Text text = TextSerializers.FORMATTING_CODE.deserialize(color(temp));
                text = text.toBuilder().onClick(TextActions.openUrl(u)).onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click me to open the link!"))).build();
                getGame().getServer().getBroadcastChannel().send(text);

                for (String command : double_rewards) {
                    getGame().getCommandManager().process(getGame().getServer().getConsole(), command.replaceAll("%player%", username));
                }
            } else {
                String temp = single_broadcast_message.replaceAll("%player%", username);
                Text text = TextSerializers.FORMATTING_CODE.deserialize(color(temp));
                text = text.toBuilder().onClick(TextActions.openUrl(u)).onHover(TextActions.showText(Text.of(TextColors.GREEN, "Click me to open the link!"))).build();
                getGame().getServer().getBroadcastChannel().send(text);

                for (String command : single_rewards) {
                    getGame().getCommandManager().process(getGame().getServer().getConsole(), command.replaceAll("%player%", username));
                }
            }
        } else {
            UUID playerID;

            if(userStorage.get().get(username).isPresent()) {
                playerID = userStorage.get().get(username).get().getUniqueId();

                if(storedVotes.containsKey(playerID)) {
                    storedVotes.put(playerID, storedVotes.get(playerID) + 1);
                } else {
                    storedVotes.put(playerID, 1);
                }

                try {
                    saveOffline();
                } catch (IOException e) {
                    getLogger().error("Couldn't save that offline player's vote!", e);
                }
            }
        }
    }

    private static String color(String string) {
        return TextSerializers.FORMATTING_CODE.serialize(Text.of(string));
    }

    public void saveOffline() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(offlineVotes.toFile());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeObject(storedVotes);
        objectOutputStream.close();
    }

    private void loadOffline() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream  = new FileInputStream(offlineVotes.toFile());
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        storedVotes = (HashMap<UUID, Integer>) objectInputStream.readObject();
        objectInputStream.close();
    }
}