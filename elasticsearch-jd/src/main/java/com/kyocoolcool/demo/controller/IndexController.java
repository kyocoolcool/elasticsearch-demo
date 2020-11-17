package com.kyocoolcool.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Chris Chen https://blog.kyocoolcool.com
 * @version 1.0
 * @since 2020/11/17 5:19 PM
 **/
@Controller
public class IndexController {

    @RequestMapping({"/","index"})
    public String index() {
        return "index";
    }
}
