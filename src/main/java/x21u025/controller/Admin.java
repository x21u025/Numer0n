package x21u025.controller;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dao.LeaderBoardDao;
import dao.RecordDao;
import dao.UserDao;
import dto.LeaderBoard;
import dto.Record;
import dto.User;
import jakarta.servlet.http.HttpServletRequest;
import util.ULog;
import util.Util;

@Controller
@Validated
public class Admin {

	@GetMapping("Admin")
	public String doGet(HttpServletRequest request, Model model,
			@RequestParam(value = "hidden", defaultValue = "false") String hidden,
			@RequestParam(value = "pass", defaultValue = "false") String pass,
			@RequestParam(value = "dec", defaultValue = "false") String dec) {
		ULog.accessPage(request);
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "USER");
				return "redirect:/";
			}
		}

		if(((User) request.getSession().getAttribute("user")).isAdmin()) {
			ArrayList<User> users = new UserDao().getAllUser();
			ArrayList<Record> records = new RecordDao().getAllRecord();
			ArrayList<LeaderBoard> scores = new LeaderBoardDao().getAllScore();

			if(!Boolean.parseBoolean(hidden)) {
				ArrayList<User> hiddenUser = (ArrayList<User>) users.stream().filter(u -> u.isHidden()).collect(Collectors.toList());
				users = (ArrayList<User>) users.stream().filter(u -> !hiddenUser.stream().anyMatch(h -> h.getId().equals(u.getId()))).collect(Collectors.toList());
				records = (ArrayList<Record>) records.stream().filter(r -> !hiddenUser.stream().anyMatch(h -> h.getId().equals(r.getId()))).collect(Collectors.toList());
				scores = (ArrayList<LeaderBoard>) scores.stream().filter(s -> !hiddenUser.stream().anyMatch(h -> h.getId().equals(s.getName()))).collect(Collectors.toList());
			}

			if(!Boolean.parseBoolean(dec)) {
				users = (ArrayList<User>) users.stream().peek(u -> u.setPass(toHash(u.getPass()))).collect(Collectors.toList());
			}

			if(!Boolean.parseBoolean(pass)) {
				users = (ArrayList<User>) users.stream().peek(u -> u.setPass(String.join("", Collections.nCopies(u.getPass().length(), "*")))).collect(Collectors.toList());
			}

			final ArrayList<Record> _records = records;
			final ArrayList<LeaderBoard> _scores = scores;

			ArrayList<model.Admin> admins = (ArrayList<model.Admin>) users.stream().map(u -> {
				Record record = _records.stream().filter(r -> r.getId().equals(u.getId())).findFirst().get();
				LeaderBoard leaderBoard = _scores.stream().filter(s -> s.getName().equals(u.getId())).findFirst().get();
				return new model.Admin(u, record, leaderBoard);
			}).collect(Collectors.toList());

			model.addAttribute("admins", admins);
			return "admin";
		} else {
			ULog.notPermission(request, "ADMIN");
			return "redirect:Mypage";
		}
	}

	@PostMapping("Admin")
	public void doPost(HttpServletRequest request) {
		ULog.accessPage(request, "[POST]");
		if(!Util.isLogin(request.getSession().getId())) {
			if(request.getSession().getAttribute("user") == null) {
				ULog.notPermission(request, "[POST] USER");
				return;
			}
		}

		if(((User) request.getSession().getAttribute("user")).isAdmin()) {
			UserDao dao = new UserDao();
			for(Entry<String, String[]> e : request.getParameterMap().entrySet()) {
				ULog.insert(request, "CHANGE_USER_DATA", "[POST] CHANGE: ID: " + e.getKey() + " HIDDEN: " + Boolean.parseBoolean(e.getValue()[0]));
				dao.setHiddenById(e.getKey(), Boolean.parseBoolean(e.getValue()[0]));
			}
		} else {
			ULog.notPermission(request, "[POST] ADMIN");
		}
	}

	private String toHash(String raw) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
			return String.format("%040x", new BigInteger(1, digest));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return raw;
	}

}
