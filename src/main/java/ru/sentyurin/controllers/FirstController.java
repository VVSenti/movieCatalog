package ru.sentyurin.controllers;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/hello")
public class FirstController {
	@GetMapping("/hello")
	public String helloPage(HttpServletRequest request) {
//		String first_name = request.getParameter("first_name");
//		String last_name = request.getParameter("last_name");
		System.out.println("Hello, !");
		return "first/hello";
	}
}