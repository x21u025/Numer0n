package x21u025.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dao.GameLogDao;
import dto.GameLog;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Watch {

	@GetMapping("Watch")
	public String doGet(HttpServletRequest request, Model model,
			@RequestParam(value = "gameId", required = false) String gameId) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}

		try {
			ArrayList<GameLog> gamelogList = new GameLogDao().getLogs(gameId);
			if(gamelogList.get(gamelogList.size() - 1).getProcess().equals("FIN")) {
				ULog.insert(request, "WATCH_GAME_FIN", gameId);
				return "redirect:Watchlist";
			}
			model.addAttribute("ws", "wss://" + request.getHeader("X-Forwarded-Server") + "/ws/Watch/" + gameId + "/" + request.getSession().getId());
			return "watch";
		} catch (Exception e) {
			ULog.error(request, e);
			return "redirect:Mypage";
		}
	}

}
