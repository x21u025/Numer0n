package x21u025.controller;

import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import dto.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;

@Controller
@Validated
public class Queue {

	@GetMapping("Queue")
	public String doGet(HttpServletRequest request, Model model)  {
		ULog.accessPage(request);
		try {
			String hash = ((User) request.getSession().getAttribute("user")).hashCode() + "";
			model.addAttribute("hash", hash);
			QueueWebSocket.hashToUser.put(hash, (User) request.getSession().getAttribute("user"));

			int speed = 500;
			Cookie cookie = Stream.of(request.getCookies()).filter(c -> c.getName().equals("speed")).findFirst().orElse(null);
			if(cookie != null) {
				speed = Integer.parseInt(cookie.getValue());
			}
			if(speed <= 0) {
				speed = 1;
			} else if(speed > 1000) {
				speed = 1000;
			}
			cookie.setValue(speed + "");
			model.addAttribute("speed", speed);

			ULog.insert(request, "NOW_SPEED", speed + "");

			model.addAttribute("ws", "wss://" + request.getHeader("X-Forwarded-Server") + "/ws/Queue/" + hash);

			return "queue";
		} catch (Exception e) {
			ULog.error(request, e);
			return "redirect:Mypage";
		}
	}

}
