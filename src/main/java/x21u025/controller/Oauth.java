package x21u025.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.azure.identity.AuthorizationCodeCredential;
import com.azure.identity.AuthorizationCodeCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.Request;

@Controller
@Validated
@RequestMapping("oauth")
public class Oauth {

	private static HashMap<String, User> users = new HashMap<>();
	final public static String COOKIE_NAME = "Xfs-Connection";

	public static boolean isUser(String cookie) {
		return users.containsKey(cookie);
	}
	public static User getUser(String cookie) {
		return users.get(cookie);
	}

	@GetMapping("redirect")
	public String code(HttpServletResponse response, @RequestParam("code") String code) throws NoSuchAlgorithmException {
		final AuthorizationCodeCredential authCodeCredential = new AuthorizationCodeCredentialBuilder()
				.clientId("CLIENTID")
				.clientSecret("SECRET")
				.authorizationCode(code)
				.redirectUrl("REDIRECTID")
				.build();

		ArrayList<String> scopes = new ArrayList<>();
		scopes.add("User.Read");
		final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes, authCodeCredential);

		final GraphServiceClient<Request> graphClient = GraphServiceClient
			.builder()
			.authenticationProvider(tokenCredentialAuthProvider)
			.buildClient();

		final User me = graphClient.me().buildRequest().get();

		System.out.printf("[SEC] [LOGIN] %s(%s)\n", me.displayName, me.userPrincipalName);

//		if(me.userPrincipalName.endsWith("@DOMAIN")) {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md5_result = md5.digest(new Date().toString().getBytes());

			String hash = me.hashCode() + String.format("%020x", new BigInteger(1, md5_result));
			users.put(hash, me);
			Cookie cookie = new Cookie(COOKIE_NAME, hash);
			cookie.setHttpOnly(true);
			cookie.setSecure(true);
			cookie.setMaxAge(604800);
			cookie.setPath("/");
			response.addCookie(cookie);
			return "redirect:/";
//		}
//		return "redirect:https://teams.microsoft.com/";
	}

	@GetMapping("is_login")
	public ModelAndView isLogin(@CookieValue(value=COOKIE_NAME, required=false, defaultValue = "") String cookie, ModelAndView mv) {
		mv.setStatus(HttpStatus.UNAUTHORIZED);
		mv.setView(new View() {
			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {}
		});
		if(users.containsKey(cookie)) {
			mv.setStatus(HttpStatus.OK);
		}
		return mv;
	}

	@GetMapping("check_login")
	public String checkLogin(@CookieValue(value=COOKIE_NAME, required=false, defaultValue = "") String cookie) {
		String url = "URL";
		if(users.containsKey(cookie)) {
			return "redirect:/oauth/is_login";
		} else {
			return "redirect:" + url;
		}
	}

}
