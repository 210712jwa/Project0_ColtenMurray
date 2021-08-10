package com.revature.controller;

import io.javalin.Javalin;
import io.javalin.http.Handler;

public class TestController implements Controller {

	
	private Handler hello = (ctx) -> {
		ctx.html("<h1>Hello World!</h1>");
	};
	
	@Override
	public void mapEndpoints(Javalin app) {
		app.get("/hello", hello); 
	}

}
