package x21u025.controller;

import java.io.UnsupportedEncodingException;
import java.util.StringJoiner;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dao.LeaderBoardDao;
import dao.RecordDao;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Register {

	@GetMapping("Register")
	public String doGet(HttpServletRequest request) {
		ULog.accessPage(request);
		return "register";
	}

	@PostMapping("Register")
	public String doPost(HttpServletRequest request, Model model,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pass", required = false) String pass,
			@RequestParam(value = "passRe", required = false) String passRe,
			@RequestParam(value = "name", required = false) String name)
	throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		System.out.println(id + ":" + pass + ":" + passRe + ":" + name);
		ULog.accessPage(request, "[POST] ID: " + id + " PASS: " + pass + " PASS(RE): " + passRe + " NAME: " + name);
		StringJoiner sj = new StringJoiner("<br>");


		if(!Util.checkId(id)) sj.add("IDを正しく入力してください");
		if(!Util.checkPassword(pass)) sj.add("Passwordを正しく入力してください");
		if(!Util.checkPassword(passRe)) sj.add("Password (Re)を正しく入力してください");
		if(!Util.checkName(name)) sj.add("Nameを正しく入力してください");

		if(Util.checkId(id) && Util.checkPassword(pass) && Util.checkPassword(passRe) && Util.checkName(name)) {
			if(pass.equals(passRe)) {
				if(new UserDao().registerByIdAndPassAndName(id, pass, name)) {
					new LeaderBoardDao().createScore(id);
					new RecordDao().createRecord(id);
					System.out.printf("[Register] %s(%s)\n", name, id);
					ULog.register(request, "[POST] " + name + "(" + id + ")");
					return "redirect:Login";
				} else {
					sj.add("IDが重複しています");
				}
			} else {
				sj.add("Passwordが一致していません");
			}
		}

		String error = sj.toString();
		ULog.insert(request, "REGISTER FAILURE", "[POST] ERROR MESSAGE: " + error);

		model.addAttribute("error", error);
		return "register";
	}

}
