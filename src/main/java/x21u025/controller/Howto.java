package x21u025.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import util.ULog;

@Controller
@Validated
public class Howto {

	@GetMapping(value = "Howto")
	public String doGet(HttpServletRequest request, Model model) {
		String ref = request.getHeader("REFERER");
		if(ref == null) {
			ref = "Mypage";
		} else {
			ref = ref.substring(ref.lastIndexOf("/") + 1);
			if(ref == "") ref = "/";
		}
		ULog.accessPage(request, ref);
		model.addAttribute("ref", ref);
		return "howto";
	}

}
