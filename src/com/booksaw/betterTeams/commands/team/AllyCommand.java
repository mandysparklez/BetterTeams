package com.booksaw.betterTeams.commands.team;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;

public class AllyCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		// checking the player is the owner
		if (player.getRank() != PlayerRank.OWNER) {
			return new CommandResponse("needOwner");
		}

		if (args.length == 0) {
			String requests = "";

			for (UUID uuid : team.getRequests()) {
				requests = requests + Team.getTeam(uuid).getDisplayName() + ChatColor.WHITE + ", ";
			}
			if (requests.length() > 2) {
				requests = requests.substring(0, requests.length() - 2);

				return new CommandResponse(true, new ReferencedFormatMessage("ally.from", requests));
			} else {
				return new CommandResponse(true, "ally.noRequests");
			}
		}

		Team toAlly = Team.getTeam(args[0]);
		if (toAlly == null) {
			return new CommandResponse("noTeam");
		} else if (toAlly == team) {
			return new CommandResponse("ally.self");
		}

		// check if they are already allies
		if (toAlly.isAlly(team.getID())) {
			return new CommandResponse("ally.already");
		}

		// checking limit
		if (team.hasMaxAllies() || toAlly.hasMaxAllies()) {
			new CommandResponse("ally.limit");
		}

		// checking if an ally request has been sent
		if (team.hasRequested(toAlly.getID())) {

			toAlly.addAlly(team.getID());
			team.addAlly(toAlly.getID());
			toAlly.removeAllyRequest(team.getID());
			team.removeAllyRequest(toAlly.getID());
			return new CommandResponse(true, "ally.success");
		}
		// sending an ally request
		toAlly.addAllyRequest(team.getID());

		return new CommandResponse(true, "ally.requested");
	}

	@Override
	public String getCommand() {
		return "ally";
	}

	@Override
	public String getNode() {
		return "ally";
	}

	@Override
	public String getHelp() {
		return "Used to request an alliance with another team";
	}

	@Override
	public String getArguments() {
		return "<team>";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		}
	}

}
