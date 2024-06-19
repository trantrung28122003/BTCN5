package com.bookstore.controller;

import com.bookstore.entity.PasswordResetToken;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.model.ResetPasswordViewModel;
import com.bookstore.services.EmailService;
import com.bookstore.services.PasswordResetTokenService;
import com.bookstore.services.RoleService;
import com.bookstore.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if ( bindingResult.hasErrors() ) {
            List<FieldError> erros = bindingResult.getFieldErrors();
            for( FieldError erro : erros ) {
                model.addAttribute(erro.getField() + "_error", erro.getDefaultMessage());
            }
            return "user/register";
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userServices.save(user);
        return "redirect:/login";
    }

    @GetMapping("/forgetPassword")
    public String forgetPassword(Model model) {
        model.addAttribute("resetPasswordViewModel", new ResetPasswordViewModel());
        return "user/forgetPassword";
    }

    @PostMapping("/sendVerificationCode")
    @ResponseBody
    public String sendVerificationCode(@RequestParam String email, @RequestParam String verificationCode) {
        try {
            String token = UUID.randomUUID().toString();
            LocalDateTime endTime = LocalDateTime.now().plusMinutes(30);
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setEmail(email);
            passwordResetToken.setToken(token);
            passwordResetToken.setEndTime(endTime);
            passwordResetTokenService.createPasswordResetToken(passwordResetToken);
            String subject = verificationCode + " lã mã xác nhận yêu cầu đặt lại mật khẩu tài khoản của bạn";
            System.out.println("emaill:"+ email + verificationCode + subject);
            emailService.sendVerificationCode(email,subject, verificationCode);
            return "ok";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/forgetPassword")
    public String forgetPassword(@ModelAttribute ResetPasswordViewModel resetPasswordViewModel, Model model) {
        System.out.println("emaill:"+ resetPasswordViewModel.getEmail());
        System.out.println("HiddenSendVerificationCode:"+ resetPasswordViewModel.getHiddenSendVerificationCode());
        System.out.println("VerificationCode:"+ resetPasswordViewModel.getVerificationCode());
        if (!resetPasswordViewModel.getHiddenSendVerificationCode().equals(resetPasswordViewModel.getVerificationCode())) {
            model.addAttribute("error", "Mã xác thực không chính xác. Vui lòng kiểm tra lại.");
            return "user/forgetPassword";
        } else {
            System.out.println("emaill:"+ resetPasswordViewModel.getEmail());
            PasswordResetToken passwordResetToken = passwordResetTokenService.findByEmail(resetPasswordViewModel.getEmail());
            if(passwordResetToken != null) {
                return "redirect:/resetPassword?token=" + passwordResetToken.getToken();
            }else {
                return "user/forgetPassword";
            }
        }
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam String token, Model model) {
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        if (passwordResetToken == null || passwordResetToken.getEndTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
            return "redirect:/forgetPassword";
        }

        model.addAttribute("resetPasswordViewModel", new ResetPasswordViewModel(passwordResetToken.getEmail()));

        return "user/resetPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@ModelAttribute ResetPasswordViewModel resetPasswordViewModel, Model model) {
        if (!resetPasswordViewModel.getNewPassword().equals(resetPasswordViewModel.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp. Vui lòng nhập lại.");
            return "user/resetPassword";
        }
        System.out.println("mat khau moi:" + resetPasswordViewModel.getNewPassword()
                + "mat khua xac nah" + resetPasswordViewModel.getConfirmPassword()
        + "emaill" + resetPasswordViewModel.getEmail());

        boolean changePassword = userServices.changePassword(resetPasswordViewModel.getEmail(), resetPasswordViewModel.getNewPassword());
        if(changePassword)
        {
            System.out.println("tyanh conggg:");
            passwordResetTokenService.deleteByEmail(resetPasswordViewModel.getEmail());
        }
        else
        {
            System.out.println("ko tim thay user");
        }
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userServices.findAllUsers();
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/editUserRole/{userId}")
    public String editUserRole(@PathVariable("userId")  Long userId, Model model) {
        User user = userServices.getUserById(userId);
        if (user == null) {
            return "redirect:/users";
        }
        List<Role> allRoles = roleService.getALlRole();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "user/editRole";
    }
    @PostMapping("/editUserRole/{userId}")
    public String updateUserRole(@PathVariable("userId")  Long userId, @RequestParam("roleIds") List<Long> roleIds) {
        User user = userServices.getUserById(userId);
        if (user == null) {

            return "redirect:/users";
        }

        List<Role> roles = roleService.getRolesByIds(roleIds);
        System.out.println("list role:" + roles.stream().toList());
        user.setRoles(roles);
        userServices.save(user);
        return "redirect:/users/" + userId;
    }


    @PostMapping("/editLockOutEnd/{userId}")
    public String editLockOutEnd(@PathVariable("userId")  Long userId) {
        User user = userServices.getUserById(userId);
        if (user == null) {
            return "redirect:/users";
        }
        LocalDateTime endTime = LocalDateTime.now().minusYears(1);
        user.setLockoutEnd(endTime);
        userServices.save(user);
        return "redirect:/users/" + userId;
    }
}
