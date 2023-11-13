package x21u025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dao.UserDao;
import dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Login {

	@GetMapping("Login")
	public String doGet(HttpServletRequest request) {
		ULog.accessPage(request);
		User user = (User) request.getSession().getAttribute("user");
		if(user != null) {
			return "redirect:Mypage";
		}
		return "login";
	}

	@PostMapping("Login")
	public String doPost(HttpServletRequest request, Model model,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pass", required = false) String pass,
			@CookieValue(value=Oauth.COOKIE_NAME, required=false, defaultValue = "") String cookie) {
		ULog.accessPage(request, "[POST] ID: " + id + " PASS: " + pass);

		if(Util.checkId(id) && Util.checkPassword(pass)) {
			User user = new UserDao().findByIdAndPassword(id, pass);
			if(user != null) {
				if(!(user.isHidden() && !user.isAdmin())) {
					HttpSession session = request.getSession(true);
					System.out.printf("[Login] %s(%s)\n", user.getName(), user.getId());
					Util.setSession(session.getId(), user.getId());
					session.setAttribute("user", user);
					if(Oauth.isUser(cookie)) {
						com.microsoft.graph.models.User me = Oauth.getUser(cookie);
						ULog.login(request, String.format("%s(%s)", me.displayName, me.userPrincipalName));
					} else {
						ULog.login(request);
					}
					return "redirect:Mypage";
				}
			} else {
				ULog.insert(request, "LOGIN FAILURE", "[POST] Not User");
			}
		} else {
			ULog.insert(request, "LOGIN FAILURE", "[POST] INCORRECT");
		}
		return "login";
	}

}
