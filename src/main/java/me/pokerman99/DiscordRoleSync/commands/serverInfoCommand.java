package me.pokerman99.DiscordRoleSync.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import me.pokerman99.DiscordRoleSync.Ref;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class serverInfoCommand implements EventListener {

	public static long upTime;

	public static List<String> users = new ArrayList<>();
	public static List<String> staff = new ArrayList<>();

	@Listener
	public void onGameStart(GameStartedServerEvent event) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(upTimeTask, 20, 1, TimeUnit.SECONDS);
	}

	Runnable upTimeTask = new Runnable() {
		public void run() {
			upTime++;
		}
	};

	@Listener
	public void onJoin(ClientConnectionEvent.Join event) {
		users.add(event.getTargetEntity().getName());
		if (event.getTargetEntity().hasPermission("discord.staff")) {
			staff.add(event.getTargetEntity().getName());
		}
		return;
	}

	@Listener
	public void onDisconnect(ClientConnectionEvent.Disconnect event) {
		users.remove(event.getTargetEntity().getName());
		if (event.getTargetEntity().hasPermission("discord.staff")) {
			staff.remove(event.getTargetEntity().getName());
		}
		return;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof MessageReceivedEvent) {
			if (((MessageReceivedEvent) event).getGuild().getId().equals(Ref.pixplus_id)) {
				String[] msg = ((MessageReceivedEvent) event).getMessage().getContentRaw().split(" ");
				if (msg[0].equalsIgnoreCase("/serverinfo")) {
					int size = msg.length;
					if (size == 1) {
						return;
					} else {
						String message = msg[1];
						String server = Sponge.getGame().getServer().getMotd().toString().replace("{", "")
								.replace("Text", "").replace("}", "");
						switch (message) {
						case "pokeverse": {
							if (!server.equalsIgnoreCase(message))
								return;
							EmbedBuilder pv = new EmbedBuilder();
							pv.setTitle("Pokeverse Server Stats").setColor(Color.ORANGE)
									.addField("Uptime", timeDiffFormat(upTime), false)
									.addField("TPS", Double.toString(Sponge.getGame().getServer().getTicksPerSecond()),
											false);
									if(!staff.isEmpty()){
									pv.addField("Staff Online",
											"`" + staff.toString().replace("[", "").replace("]", "") + " `" + "\n"
													+ staff.size(),
											false);
									}
							((MessageReceivedEvent) event).getChannel().sendMessage(pv.build()).queue();
							break;
						}
						case "pokedash": {
							if (!server.equalsIgnoreCase(message))
								return;
							EmbedBuilder pd = new EmbedBuilder();
							pd.setTitle("Pokedash Server Stats").setColor(Color.BLUE)
									.addField("Uptime", timeDiffFormat(upTime), false)
									.addField("TPS", Double.toString(Sponge.getGame().getServer().getTicksPerSecond()),
											false);
									if(!staff.isEmpty()){
									pd.addField("Staff Online",
											"`" + staff.toString().replace("[", "").replace("]", "") + " `" + "\n"
													+ staff.size(),
											false);
									}
							((MessageReceivedEvent) event).getChannel().sendMessage(pd.build()).queue();
							break;
						}
						case "pokeclub": {
							if (!server.equalsIgnoreCase(message))
								return;
							EmbedBuilder pc = new EmbedBuilder();
							pc.setTitle("Pokeclub Server Stats").setColor(Color.cyan)
									.addField("Uptime", timeDiffFormat(upTime), false)
									.addField("TPS", Double.toString(Sponge.getGame().getServer().getTicksPerSecond()),
											false);
									if(!staff.isEmpty()){
									pc.addField("Staff Online",
											"`" + staff.toString().replace("[", "").replace("]", "") + " `" + "\n"
													+ staff.size(),
											false);
									}
							((MessageReceivedEvent) event).getChannel().sendMessage(pc.build()).queue();
							break;
						}
						case "pokelegends": {
							if (!server.equalsIgnoreCase(message))
								return;
							EmbedBuilder pl = new EmbedBuilder();
							pl.setTitle("Pokelegends Server Stats").setColor(Color.YELLOW)
									.addField("Uptime", timeDiffFormat(upTime), false)
									.addField("TPS", Double.toString(Sponge.getGame().getServer().getTicksPerSecond()),
											false);
									if(!staff.isEmpty()){
									pl.addField("Staff Online",
											"`" + staff.toString().replace("[", "").replace("]", "") + " `" + "\n"
													+ staff.size(),
											false);
									}
							((MessageReceivedEvent) event).getChannel().sendMessage(pl.build()).queue();
							break;
						}
						}
					}
					return;
				}
			}
		}
	}

	public static String timeDiffFormat(long timeDiff) {
		int seconds = (int) timeDiff % 60;
		timeDiff = timeDiff / 60;
		int minutes = (int) timeDiff % 60;
		timeDiff = timeDiff / 60;
		int hours = (int) timeDiff % 24;
		timeDiff = timeDiff / 24;
		int days = (int) timeDiff;

		String timeFormat;

		if (days > 7) {
			timeFormat = days + " days";
		} else if (days > 0) {
			timeFormat = days + "d " + hours + "h";
		} else if (days == 0 && hours > 0) {
			timeFormat = hours + "h " + minutes + "m " + seconds + "s";
		} else if (days == 0 && hours == 0 && minutes > 0) {
			timeFormat = minutes + "m " + seconds + "s";
		} else {
			timeFormat = seconds + "s";
		}

		return timeFormat;
	}

}