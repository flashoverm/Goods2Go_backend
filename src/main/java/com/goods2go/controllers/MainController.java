package com.goods2go.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

	//not used
  @RequestMapping("/")
  @ResponseBody
  public String index() {
    return "<h1>Goods2go</h1>";
  }

}
