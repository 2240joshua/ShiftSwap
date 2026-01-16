package com.shiftswap.shiftswap.controller;

import com.shiftswap.shiftswap.model.Shift;
import com.shiftswap.shiftswap.model.User;
import com.shiftswap.shiftswap.repository.ShiftRepository;
import com.shiftswap.shiftswap.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public UserController(UserRepository userRepository, ShiftRepository shiftRepository) {
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("/{id}/shifts")
    public List<Shift> getUserShifts(@PathVariable Long id) {
        return shiftRepository.findByAssignedUser_Id(id);
    }
}
