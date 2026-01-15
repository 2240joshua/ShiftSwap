package com.shiftswap.shiftswap.controller;

import com.shiftswap.shiftswap.model.Shift;
import com.shiftswap.shiftswap.model.User;
import com.shiftswap.shiftswap.repository.ShiftRepository;
import com.shiftswap.shiftswap.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shifts")
public class ShiftController {

    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    public ShiftController(ShiftRepository shiftRepository, UserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Shift createShift(@RequestBody Shift shift) {
        return shiftRepository.save(shift);
    }

    @GetMapping("/{id}")
    public Shift getShift(@PathVariable Long id) {
        return shiftRepository.findById(id).orElseThrow();
    }

    @PostMapping("/{shiftId}/assign/{userId}")
    public Shift assignShift(@PathVariable Long shiftId, @PathVariable Long userId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        shift.setAssignedUser(user);   // requires setter on Shift
        return shiftRepository.save(shift);
    }
}
