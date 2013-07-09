package nl.siegmann.ehcachetag.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/index.html")
	public String doHomepage(Model model) {
		return "home";
	}
}
