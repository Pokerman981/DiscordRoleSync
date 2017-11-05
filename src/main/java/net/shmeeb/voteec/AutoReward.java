package net.shmeeb.voteec;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import com.vexsoftware.votifier.sponge.event.VotifierEvent;

public class AutoReward implements CommandExecutor {

	public Main plugin1;

	public AutoReward(Main pluginInstance) {
		this.plugin1 = pluginInstance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		try {
			Player player = Sponge.getGame().getServer().getPlayer(src.getName()).get();
			UUID uuid = UUID.fromString(player.getIdentifier().toString());
			String sql = "INSERT INTO voterecords(uuid, votes) VALUES(?,?)";
			String select = "SELECT id, uuid, votes FROM voterecords WHERE uuid = '" + uuid + "';";
			Connection conn = this.connectDB();
			ResultSet resultSet = conn.createStatement().executeQuery(select);
			int votes = resultSet.getInt(3) + 1;
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			String update = "update voterecords set votes='" + votes + "' where uuid = '" + uuid + "'";
			conn.prepareStatement(update).executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CommandResult.success();
	}

	@Listener
	public void onJoin(ClientConnectionEvent.Join event) throws SQLException {
		Connection conn = this.connectDB();
		String squid;
		String uuid = event.getTargetEntity().getIdentifier().toString();
		String sql = "INSERT INTO voterecords(uuid, votes) VALUES(?,?)";
		String select = "SELECT id, uuid, votes FROM voterecords WHERE uuid = '" + uuid + "';";
		ResultSet resultSet = conn.createStatement().executeQuery(select);
		if (resultSet.isClosed()) {
			PreparedStatement prepstmt = conn.prepareStatement(sql);
			prepstmt.setString(1, uuid);
			prepstmt.setInt(2, 1);
			prepstmt.executeUpdate();
			MessageChannel.TO_ALL.send(Text.of("test2"));
		} else if (resultSet.getString(2).equals(uuid)) { //I could change this but meh
			conn.close();
		}
		int rsmd = resultSet.getMetaData().getColumnCount();
	}

	@Listener
	public void onVote(VotifierEvent e) throws SQLException {
		Player player = Sponge.getGame().getServer().getPlayer(e.getVote().getUsername()).get();
		UUID uuid = UUID.fromString(player.getIdentifier().toString());
		String sql = "INSERT INTO voterecords(uuid, votes) VALUES(?,?)";
		String select = "SELECT id, uuid, votes FROM voterecords WHERE uuid = '" + uuid + "';";
		Connection conn = this.connectDB();
		ResultSet resultSet = conn.createStatement().executeQuery(select);
		int votes = resultSet.getInt(3) + 1;
		PreparedStatement prepstmt = conn.prepareStatement(sql);
		String update = "update voterecords set votes='" + votes + "' where uuid = '" + uuid + "'";
		conn.prepareStatement(update).executeUpdate();
		conn.close();
		return;

	}

	@Listener
	public void onStart(GameStartedServerEvent event) {
		createDB();
		createTable();
	}

	private Connection connectDB() {
		String url = "jdbc:sqlite:./config/voteec/voterecords.db";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public void createDB() {
		String url = "jdbc:sqlite:./config/voteec/voterecords.db";
		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				MessageChannel.TO_ALL.send(Text.of("This driver name is " + meta.getDriverName()));
				MessageChannel.TO_ALL.send(Text.of("A new database has been made"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createTable() {
		String url = "jdbc:sqlite:./config/voteec/voterecords.db";

		String sql = "CREATE TABLE IF NOT EXISTS voterecords (\n" + "		id integer PRIMARY KEY,\n"
				+ "		uuid UUID,\n" + "		votes integer);";

		try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
