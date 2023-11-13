package x21u025.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dao.AccessLogDao;
import dao.UserDao;
import dto.User;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;
import util.Util;

@Controller
@Validated
public class AccessLog {


	@GetMapping("/AccessLog")
	public String doGet(HttpServletRequest request, Model model,
			@RequestParam(value = "page", defaultValue = "1") String page,
			@RequestParam(value = "size", defaultValue = "100") String size,
			@RequestParam(value = "range", required = false) String range) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}

		if(((User) request.getSession().getAttribute("user")).isAdmin()) {
//			if(range != null && range.matches("[0-9]*-[0-9]")) {
//
//			}
			model.addAttribute("logs", page.equalsIgnoreCase("all") ? AccessLogDao.getAllLogs() : AccessLogDao.getLogs(page, size));
			model.addAttribute("users", new UserDao().getAllUser());
			model.addAttribute("logic", new AccessLogLogic());

			return "accesslog";
		} else {
			ULog.notPermission(request, "ADMIN");
			return "redirect:Mypage";
		}

	}

}

class AccessLogLogic {
	public String getUserName(ArrayList<User> users, dto.AccessLog al) {
		try {
			User user = users.stream().filter(p -> p.getId().equals(al.getUser())).findFirst().get();
			return user.getName() + "(" + user.getId() + ")";
		} catch (Exception e) {}
		return al.getUser();
	}
}