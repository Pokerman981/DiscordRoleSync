package me.pokerman99.DiscordRoleSync;

import javax.security.auth.login.LoginException;

import me.pokerman99.DiscordRoleSync.commands.broadcastCommand;
import me.pokerman99.DiscordRoleSync.commands.commandCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;



public class discord extends ListenerAdapter {
	
	public static JDA jda;

	public static void Login(String server) {
		try {
			switch (server){
			case "pokedash":{
				jda = new JDABuilder(AccountType.BOT).setToken(Ref.pokedash_token).buildBlocking();
				jda.addEventListener(new roleSync(new Main()));
				jda.addEventListener(new commandCommand());
				jda.addEventListener(new broadcastCommand());
				break;
			}
			case "pokelegends":{
				jda = new JDABuilder(AccountType.BOT).setToken(Ref.pokelegends_token).buildBlocking();
				jda.addEventListener(new roleSync(new Main()));
				jda.addEventListener(new commandCommand());
				jda.addEventListener(new broadcastCommand());
				break;
			}
			case "pokeclub":{
				jda = new JDABuilder(AccountType.BOT).setToken(Ref.pokeclub_token).buildBlocking();
				jda.addEventListener(new roleSync(new Main()));
				jda.addEventListener(new commandCommand());
				jda.addEventListener(new broadcastCommand());
				break;
			}
			case "pokeverse":{
				jda = new JDABuilder(AccountType.BOT).setToken(Ref.pokeverse_token).buildBlocking();
				jda.addEventListener(new roleSync(new Main()));
				jda.addEventListener(new commandCommand());
				jda.addEventListener(new broadcastCommand());
				break;
			}
			}
			//jda.addEventListener(new serverInfoCommand());
		} catch (LoginException | IllegalArgumentException | RateLimitedException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
	}
	
	public static void Logout() {
		jda.removeEventListener(new roleSync(new Main()));
		jda.removeEventListener(new commandCommand());
		jda.removeEventListener(new broadcastCommand());
		jda.shutdown();
	}

}
