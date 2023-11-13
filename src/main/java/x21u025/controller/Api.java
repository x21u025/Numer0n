package x21u025.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@Validated
@RequestMapping("api")
public class Api {

	@PostMapping("websocket/log")
	public ModelAndView setWebSocketLog(HttpServletRequest request, @RequestParam("message") String message, ModelAndView mv) {
		mv.setStatus(HttpStatus.OK);
		mv.setView(new View() {
			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {}
		});

//		ULog.insert(request, "WEBSOCKET_ERROR", message);
		System.out.println(message);
		return mv;
	}
}
