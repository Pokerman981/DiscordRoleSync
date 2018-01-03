package me.pokerman99.DiscordServerStats;

import javax.security.auth.login.LoginException;

import me.pokerman99.DiscordServerStats.commands.serverInfoCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;



public class discord extends ListenerAdapter {

	public static JDA jda;

	public static void Main() {
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(Ref.main_token).setAutoReconnect(true).buildAsync();
			jda.addEventListener(new serverInfoCommand());
			//jda.getPresence().setGame(Game.playing("A game"));
		} catch (LoginException | IllegalArgumentException | RateLimitedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
