package me.pokerman99.DiscordRoleSync.commands;

import org.spongepowered.api.Sponge;

import me.pokerman99.DiscordRoleSync.Ref;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class commandCommand implements EventListener {

	@Override
	public void onEvent(Event event) {
		if (event instanceof GuildMessageReceivedEvent) {
			if (((GuildMessageReceivedEvent) event).getGuild().getId().equals(Ref.pixplus_id) == false)
				return;
			String[] cmd = ((GuildMessageReceivedEvent) event).getMessage().getContentRaw().split(" ");
			if (cmd.length == 1)
				return;
			if (cmd[0].equals(".command")) {
				if (((GuildMessageReceivedEvent) event).getAuthor().getId().equals(Ref.owner_id)
						|| ((GuildMessageReceivedEvent) event).getAuthor().getId().equals("126427288496504834")) {
					String message = ((GuildMessageReceivedEvent) event).getMessage().getContentRaw().replace(".command ", "");
					Sponge.getCommandManager().process(Sponge.getServer().getConsole(), message);
				}
			}
		}
	}

}
