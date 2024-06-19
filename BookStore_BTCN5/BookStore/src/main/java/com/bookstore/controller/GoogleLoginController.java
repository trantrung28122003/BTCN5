package com.bookstore.controller;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
public class GoogleLoginController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private UserServices userServices;

    @Autowired
    public GoogleLoginController(ClientRegistrationRepository clientRegistrationRepository, UserServices userServices) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userServices = userServices;
    }

    @GetMapping("/login/oauth2/code/google")
    public String googleSignIn(OAuth2AuthenticationToken oauth2AuthenticationToken) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        String email = oauth2AuthenticationToken.getPrincipal().getAttribute("email");
        User user = userServices.getUserByEmail(email);

        if (user == null) {
            // Create new user if not exists
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Set username based on your application's logic
            user.setName(oauth2AuthenticationToken.getPrincipal().getAttribute("name"));
            userServices.save(user);
        }

        // Set authentication in Security Context
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Redirect to home page after successful login
        return "redirect:/";
    }

}

