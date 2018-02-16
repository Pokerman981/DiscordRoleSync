package me.pokerman99.DiscordRoleSync;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import com.google.common.reflect.TypeToken;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class roleSync implements net.dv8tion.jda.core.hooks.EventListener, CommandExecutor {

	public Main plugin;

	public roleSync(Main pluginInstance) {
		this.plugin = pluginInstance;
	}
	
	public static List<String> banned = new ArrayList<>();

	public static EmbedBuilder PCRules(){
		EmbedBuilder PCRules = new EmbedBuilder();
		PCRules.setTitle("**Pokeclub Rules**").setColor(Color.decode("#1e7d72")).setThumbnail("https://i.gyazo.com/21380d908820fc0c78e95a813e66b5cd.png")
		.setDescription("\n**1.** No cursing of any kinds \n**2.** No racism/slurs of any kinds \n**3.** No advertising \n**4.** No caps lock \n**5.** English Only \n**6.** NO Impersonating Staff/Players \n**7.** No Spamming \n**8.** Common Sense\n\nIf **ANY** of these rules are broken your speaking permission will be removed **indefinitely** from the chat(s). You will **also** receive a punishment of the **staffs choosing** on the server.");
		return PCRules;
	}
	public static EmbedBuilder PDRules(){
		EmbedBuilder PDRules = new EmbedBuilder();
		PDRules.setTitle("**Pokedash Rules**").setColor(Color.decode("#3cb0d6")).setThumbnail("https://i.gyazo.com/13255ea852d4551df36e9c1c9c3d4e92.png")
		.setDescription("\n**1.** No cursing of any kinds \n**2.** No racism/slurs of any kinds \n**3.** No advertising \n**4.** No caps lock \n**5.** English Only \n**6.** NO Impersonating Staff/Players \n**7.** No Spamming \n**8.** Common Sense\n\nIf **ANY** of these rules are broken your speaking permission will be removed **indefinitely** from the chat(s). You will **also** receive a punishment of the **staffs choosing** on the server.");
		return PDRules;
	}
	public static EmbedBuilder PLRules(){
		EmbedBuilder PLRules = new EmbedBuilder();
		PLRules.setTitle("**Pokelegends Rules**").setColor(Color.decode("#FF4500")).setThumbnail("https://i.gyazo.com/70f77a6428a14045674f94ccd5134941.png")
		.setDescription("\n**1.** No cursing of any kinds \n**2.** No racism/slurs of any kinds \n**3.** No advertising \n**4.** No caps lock \n**5.** English Only \n**6.** NO Impersonating Staff/Players \n**7.** No Spamming \n**8.** Common Sense\n\nIf **ANY** of these rules are broken your speaking permission will be removed **indefinitely** from the chat(s). You will **also** receive a punishment of the **staffs choosing** on the server.");
		return PLRules;
	}
	public static EmbedBuilder PVRules(){
		EmbedBuilder PVRules = new EmbedBuilder();
		PVRules.setTitle("**Pokeverse Rules**").setColor(Color.yellow).setThumbnail("https://i.gyazo.com/911b1ee65ef59f36343d5bc5da046d12.png")
		.setDescription("\n**1.** No cursing of any kinds \n**2.** No racism/slurs of any kinds \n**3.** No advertising \n**4.** No caps lock \n**5.** English Only \n**6.** NO Impersonating Staff/Players \n**7.** No Spamming \n**8.** Common Sense\n\nIf **ANY** of these rules are broken your speaking permission will be removed **indefinitely** from the chat(s). You will **also** receive a punishment of the **staffs choosing** on the server.");
		return PVRules;
	}
	
	public static List<String> pins = new ArrayList<>();
	public static List<String> linked = new ArrayList<>();
	
	@Listener
	public void onGameStart(GameStartedServerEvent event){
		try {
			List<String> pin = Main.config().getNode("pins").getList(TypeToken.of(String.class));
			pins.addAll(pin);
			List<String> links = Main.config().getNode("linked").getList(TypeToken.of(String.class));
			linked.addAll(links);
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
			executor.scheduleAtFixedRate(saveFile, 2, 5, TimeUnit.MINUTES);
		} catch (ObjectMappingException e) {e.printStackTrace();}
	}
	
	@Override
	public void onEvent(Event event) {
		if (event instanceof GuildMessageReceivedEvent){
			if (((GuildMessageReceivedEvent) event).getGuild().getId().equals(Ref.pixplus_id) == false) return;
				String[] cmd = ((GuildMessageReceivedEvent) event).getMessage().getContentRaw().split(" ");
				if (cmd.length == 1) return;
				if (cmd[0].equals(".link") || cmd[0].equals("*link")){
					String server = Sponge.getGame().getServer().getMotd().toString().replace("{", "")
							.replace("Text", "").replace("}", "").toLowerCase();
					switch (server){
					case "pokeverse":{
						Array.set(cmd, 0, "");
						if (pins.toString().contains(cmd[1])){
							pins.remove(cmd[1]);
							User user = ((GuildMessageReceivedEvent) event).getAuthor();
							((GuildMessageReceivedEvent) event).getGuild().getMember(((GuildMessageReceivedEvent) event).getAuthor()).getGuild().getController()
							.addSingleRoleToMember(((GuildMessageReceivedEvent) event).getGuild().getMemberById(((GuildMessageReceivedEvent) event).getAuthor().getId()), ((GuildMessageReceivedEvent) event).getGuild().getRoleById("401183132327608333")).submit();
							user.openPrivateChannel()
							.queue((channel) -> channel.sendMessage(PVRules().build()).submit());
							((GuildMessageReceivedEvent) event).getMessage().delete().submit();
							}
						break;
						}
					case "pokedash":{
						Array.set(cmd, 0, "");
						if (pins.toString().contains(cmd[1])){
							pins.remove(cmd[1]);
							User user = ((GuildMessageReceivedEvent) event).getAuthor();
							((GuildMessageReceivedEvent) event).getGuild().getMember(((GuildMessageReceivedEvent) event).getAuthor()).getGuild().getController()
							.addSingleRoleToMember(((GuildMessageReceivedEvent) event).getGuild().getMemberById(((GuildMessageReceivedEvent) event).getAuthor().getId()), ((GuildMessageReceivedEvent) event).getGuild().getRoleById("401183019932581888")).submit();
							user.openPrivateChannel()
							.queue((channel) -> channel.sendMessage(PDRules().build()).submit());
							((GuildMessageReceivedEvent) event).getMessage().delete().submit();
							}
						break;
						}
					case "pokeclub":{
						Array.set(cmd, 0, "");
						if (pins.toString().contains(cmd[1])){
							pins.remove(cmd[1]);
							User user = ((GuildMessageReceivedEvent) event).getAuthor();
							((GuildMessageReceivedEvent) event).getGuild().getMember(((GuildMessageReceivedEvent) event).getAuthor()).getGuild().getController()
							.addSingleRoleToMember(((GuildMessageReceivedEvent) event).getGuild().getMemberById(((GuildMessageReceivedEvent) event).getAuthor().getId()), ((GuildMessageReceivedEvent) event).getGuild().getRoleById("401183075918151682")).submit();
							user.openPrivateChannel()
							.queue((channel) -> channel.sendMessage(PCRules().build()).submit());
							((GuildMessageReceivedEvent) event).getMessage().delete().submit();
							}
						break;
						}
					case "pokelegends":{
						Array.set(cmd, 0, "");
						if (pins.toString().contains(cmd[1])){
							pins.remove(cmd[1]);
							User user = ((GuildMessageReceivedEvent) event).getAuthor();
							((GuildMessageReceivedEvent) event).getGuild().getMember(((GuildMessageReceivedEvent) event).getAuthor()).getGuild().getController()
							.addSingleRoleToMember(((GuildMessageReceivedEvent) event).getGuild().getMemberById(((GuildMessageReceivedEvent) event).getAuthor().getId()), ((GuildMessageReceivedEvent) event).getGuild().getRoleById("401183246106361856")).submit();
							user.openPrivateChannel()
							.queue((channel) -> channel.sendMessage(PLRules().build()).submit());
							((GuildMessageReceivedEvent) event).getMessage().delete().submit();
							}
						}
					break;
					}
				}
			
		}
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src.hasPermission("discord.donator")){
			if (linked.contains(src.getIdentifier()) == false){
				Random random = new Random();
				String id = String.format("%04d", random.nextInt(10000));
				sendMessage(src, "&aTo finish linking your profile to discord type the command '.link (pin)' in any channel on the pixelmon+ discord");
				sendMessage(src, "&aYour custom pin number is " + id);
				pins.add(id);
				linked.add(src.getIdentifier());
				plugin.rootNode.getNode("records", src.getIdentifier(), "name").setValue(src.getName());
				plugin.rootNode.getNode("records", src.getIdentifier(), "pin").setValue(id);
				plugin.rootNode.getNode("pins").setValue(pins);
				plugin.rootNode.getNode("linked").setValue(linked);
				try {
					plugin.save();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				sendMessage(src, "&cYou've already linked your account or requested a pin!");
			}
		}
		return CommandResult.success();
	}
	
	@Listener
	public void onShutDown(GameStoppingEvent event){
		try {
			plugin.rootNode.getNode("pins").setValue(pins);
			plugin.rootNode.getNode("linked").setValue(linked);
			plugin.save();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	Runnable saveFile = new Runnable() {
		public void run() {
			Task.Builder voterecord = Task.builder().async().name("discordrolesyncsave");
			voterecord.execute(task -> {
				try {
					plugin.rootNode.getNode("pins").setValue(pins);
					plugin.rootNode.getNode("linked").setValue(linked);
					plugin.save();
					plugin.getLogger().info("Saved Config");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).submit(plugin.getPlugin());
		}
	};
	
	public static String color(String string) {
		return org.spongepowered.api.text.serializer.TextSerializers.FORMATTING_CODE
				.serialize(org.spongepowered.api.text.Text.of(string));
	}

	public static void sendMessage(CommandSource sender, String message) {
		if (sender == null)
			return;
		sender.sendMessage(
				org.spongepowered.api.text.serializer.TextSerializers.FORMATTING_CODE.deserialize(color(message)));
	};
	

}
