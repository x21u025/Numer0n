package x21u025.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import dao.LeaderBoardDao;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;

@Controller
@Validated
public class Title implements ErrorController {

	@GetMapping("/")
	public String doGet(HttpServletRequest request, Model model, @CookieValue(value=Oauth.COOKIE_NAME, required=false, defaultValue = "") String cookie) {
		if(Oauth.isUser(cookie)) {
			com.microsoft.graph.models.User me = Oauth.getUser(cookie);
			ULog.accessPage(request, String.format("%s(%s)", me.displayName, me.userPrincipalName));
		} else {
			ULog.accessPage(request);
		}
		model.addAttribute("lbList", new LeaderBoardDao().nameAndScore());

		return "title";
	}

	@GetMapping("Title")
	public String redirectIndex() {
		return "redirect:/";
	}

	@GetMapping("error")
	public String error() {
		return "redirect:/";
	}
}
