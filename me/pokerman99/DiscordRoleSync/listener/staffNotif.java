package me.pokerman99.DiscordRoleSync.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

public class staffNotif implements EventListener{

	public static long upTime;
	public static long noStaff;

	public static List<String> users = new ArrayList<>();
	public static List<String> staff = new ArrayList<>();

	@Listener
	public void onGameStart(GameStartedServerEvent event) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(upTimeTask, 20, 1, TimeUnit.MINUTES);
	}

	Runnable upTimeTask = new Runnable() {
		public void run() { 
			
		}
	};

	
	@Override
	public void onEvent(Event event) {
		
		
	}

}
