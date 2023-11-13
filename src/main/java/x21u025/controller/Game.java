package x21u025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import dto.User;
import exception.Numer0nException;
import jakarta.servlet.http.HttpServletRequest;
import numer0n.Numer0n;
import numer0n.Numer0n.Phase;
import numer0n.data.Numer0nData;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Game {

	@GetMapping("Game")
	public String doGet(HttpServletRequest request, Model model) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}
		try {
			Numer0n numer0n = Numer0nData.getAllGame().stream().filter(e -> e.getValue().isPlayer(((User) request.getSession().getAttribute("user")).getId())).findFirst().get().getValue();
			if(numer0n.getPhase() == Phase.FIN) throw new Numer0nException("This Game is FIN.");
			String gameId = numer0n.getGameId();
			ULog.startGame(request, gameId);
			model.addAttribute("ws", "wss://" + request.getHeader("X-Forwarded-Server") + "/ws/Game/" + gameId + "/" + request.getSession().getId());
		} catch (Exception e) {
			ULog.error(request, e);
			return "redirect:Mypage";
		}

		String userId = (String) ((User) request.getSession().getAttribute("user")).getId();
		model.addAttribute("userId", userId);
		return "game";
	}

}
