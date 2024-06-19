package com.bookstore.services;

import com.bookstore.entity.User;
import com.bookstore.repository.IRoleRepository;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
        Long userId = userRepository.getUserIdByUserName(user.getUsername());
        Long roleId = roleRepository.getRoleIdByName("USER");
        if ( roleId != null && userId != 0 ) {
            userRepository.addRoleToUser(userId, roleId);
        }
    }

    public User getUserByEmail (String email)
    {
        return userRepository.getUserByEmail(email);
    }
    public User getUserById(Long id) {
        return userRepository.getUserById(id);
    }
    public boolean changePassword(String email, String newPassword) {
        User userByEmail = userRepository.getUserByEmail(email);
        if (userByEmail != null) {
            userByEmail.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(userByEmail);
            return true;
        }
        return false;
    }
}
