package x21u025.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import dao.GameLogDao;
import dao.GameLogsDao;
import dao.UserDao;
import dto.GameLog;
import dto.GameLogs;
import dto.User;
import jakarta.servlet.http.HttpServletRequest;
import numer0n.data.Numer0nData;
import util.ULog;
import util.Util;

@Controller
@Validated
public class WatchList {


	@GetMapping("Watchlist")
	public String doGet(HttpServletRequest request, Model model) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}

		List<String> nowPlayingList = Numer0nData.getAllGame().stream().map(g -> g.getKey()).collect(Collectors.toList());
		List<GameLogs> nowPlayingGameList = new GameLogsDao().getAllGameLogs().stream().filter(g -> nowPlayingList.contains(g.getId())).collect(Collectors.toList());
		model.addAttribute("nowPlaying", nowPlayingGameList);

		ArrayList<User> users = new UserDao().getAllUser();
		HashMap<String, User[]> battleUsers = new HashMap<>();
		for(GameLogs logs : nowPlayingGameList) {
			String id = logs.getId();
			ArrayList<GameLog> thisGameLogs = new GameLogDao().getLogs(id);
			GameLog[] thisGamePlayerLogs = thisGameLogs.stream().filter(g -> g.getProcess().equals("PLAYER")).toArray(GameLog[]::new);

			User[] players = new User[2];
			for(GameLog pl : thisGamePlayerLogs) {
				if(pl.getNumber().equals("1")) {
					players[0] = users.stream().filter(p -> p.getId().equals(pl.getPlayer())).findFirst().orElse(null);
				} else if(pl.getNumber().equals("2")) {
					players[1] = users.stream().filter(p -> p.getId().equals(pl.getPlayer())).findFirst().orElse(null);
				}
			}
			battleUsers.put(id, players);
		}
		model.addAttribute("battleUsers", battleUsers);

		return "watchlist";
	}

}
