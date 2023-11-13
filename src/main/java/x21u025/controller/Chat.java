package x21u025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@Validated
public class Chat {

		@GetMapping("chat")
		public String doGet(HttpServletRequest request, Model model,
			@RequestParam(value = "sessionId", defaultValue = "null") String sessionId) {
			return "chat";
		}
}
