package com.example.ormi5finalteam1.controller.thymeleaf_controller;

import com.example.ormi5finalteam1.common.exception.BusinessException;
import com.example.ormi5finalteam1.common.exception.ErrorCode;
import com.example.ormi5finalteam1.domain.user.Provider;
import com.example.ormi5finalteam1.domain.user.Role;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminInfoController {

    @GetMapping("/users")
    public String getUserList(@AuthenticationPrincipal Provider provider) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return "admin/user-list";
    }

    @GetMapping("/posts")
    public String getPostList(@AuthenticationPrincipal Provider provider) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return "admin/admin-post-list";
    }

    @GetMapping("/posts/{id}")
    public String getPostById(@AuthenticationPrincipal Provider provider) {

        if (!provider.role().equals(Role.ADMIN)) throw new BusinessException(ErrorCode.HAS_NO_AUTHORITY);
        return "admin/admin-post-detail";
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {

        model.addAttribute("errorMessage", e.getMessage());
        return "tests/error";
    }
}
