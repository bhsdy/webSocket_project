package com.cn.websocket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractBaseController {

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> create(){
		return ok("11111111");
	}


}
