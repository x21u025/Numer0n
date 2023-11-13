package x21u025.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import dao.GameLogDao;
import dao.GameLogsDao;
import dao.UserDao;
import dto.GameLog;
import dto.GameLogs;
import dto.User;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Log {

	@GetMapping("Log")
	public String doGet(HttpServletRequest request, Model model,
			@RequestParam(value = "gameId", required = false) String gameId,
			@RequestParam(value = "userId", required = false) String userId,
			@SessionAttribute("user") User user) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}

		if(((User) request.getSession().getAttribute("user")).isAdmin()) {
			String header = "Log";
			ArrayList<User> users = new UserDao().getAllUser();
			if(gameId == null && userId == null) {
				ArrayList<GameLogs> gameLogs = new GameLogsDao().getAllGameLogs();
				model.addAttribute("gamelogs", gameLogs);

				HashMap<String, User[]> battleUsers = new HashMap<>();
				for(GameLogs logs : gameLogs) {
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
				ULog.accessPage(request, "DEFALUT");
			} else if(userId != null) {
				GameLogDao dao = new GameLogDao();
				ArrayList<GameLog> allGameLogs = dao.getAllLogs();
				String[] gameIds = allGameLogs.stream().filter(g -> g.getProcess().equals("PLAYER")).filter(g -> g.getPlayer().equals(userId)).map(l -> l.getGameId()).distinct().toArray(String[]::new);
				ArrayList<GameLog> userGameLogs = new ArrayList<GameLog>();
				for(String gi : gameIds) {
					userGameLogs.addAll(dao.getLogs(gi));
				}
				model.addAttribute("gamelog", userGameLogs);
				ULog.accessPage(request, "USER: " + userId);
				User thisUser = new UserDao().findUserById(userId);
				header = thisUser.getName() + "(" + thisUser.getId() + ")";
			} else {
				ArrayList<GameLog> thisGameLogs = new GameLogDao().getLogs(gameId);
				model.addAttribute("gamelog", thisGameLogs);
				ULog.accessPage(request, "GAME: " + gameId);
				header = thisGameLogs.get(0).getGameId();
			}

			model.addAttribute("isGameLogs", gameId == null);
			model.addAttribute("isUserLogs", userId != null);
			model.addAttribute("userId", userId);
			model.addAttribute("users", users);
			model.addAttribute("header", header);
			model.addAttribute("logic", new LogLogic());

			return "log";
		} else {
			ULog.notPermission(request, "ADMIN");
			return "redirect:Mypage";
		}

	}

}

class LogLogic {
	public User getUser(ArrayList<User> users, GameLog gl) {
		try {
			return users.stream().filter(p -> p.getId().equals(gl.getPlayer())).findFirst().get();
		} catch (Exception e) {}
		return null;
	}
}
