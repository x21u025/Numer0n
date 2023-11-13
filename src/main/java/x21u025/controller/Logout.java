package x21u025.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import dto.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Logout {

	@GetMapping("Logout")
	public String doGet(HttpServletRequest request) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = Util.getUserBySession(session.getId());
		if(user != null) {
			System.out.printf("[Logout] %s(%s)\n", user.getName(), user.getId());
			ULog.logout(request);

			Util.removeSession(session.getId());
		}
		session.removeAttribute("user");
		session.invalidate();
		return "redirect:/";
	}

}
