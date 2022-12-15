package com.libsgh.mybatis.gen.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName CustomErrorController
 * @Description TODO
 * @Author Libs
 * @Date 2022/12/15 10:29
 * @Version 1.0
 */
@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("statusCode", statusCode);
            model.addAttribute("reasonPhrase", HttpStatus.valueOf(statusCode).getReasonPhrase());
        }
        return "error";
    }
}
