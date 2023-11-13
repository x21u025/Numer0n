package x21u025.controller;

import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import dao.LeaderBoardDao;
import dto.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Mypage {

	@GetMapping("Mypage")
	public String doGet(HttpServletRequest request, HttpServletResponse response, Model model) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}
		model.addAttribute("lbList",new LeaderBoardDao().nameAndScore());
		model.addAttribute("score", new LeaderBoardDao().getScore(((User) request.getSession().getAttribute("user")).getId()));
		int speed = 500;
		Cookie speedCookie = Stream.of(request.getCookies()).filter(c -> c.getName().equals("speed")).findFirst().orElse(null);
		if(speedCookie == null) {
			speedCookie = new Cookie("speed", "500");
			speedCookie.setMaxAge(60 * 60 * 24 * 7);
			response.addCookie(speedCookie);
		}
		speed = Integer.parseInt(speedCookie.getValue());
		model.addAttribute("speed", speed);
		model.addAttribute("ws", "wss://" + request.getHeader("X-Forwarded-Server") + "/ws/Chat/" + request.getSession().getId());
		return "mypage";
	}

}
