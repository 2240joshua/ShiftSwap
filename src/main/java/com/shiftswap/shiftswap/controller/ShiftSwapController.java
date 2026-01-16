package com.shiftswap.shiftswap.controller;

import com.shiftswap.shiftswap.model.Shift;
import com.shiftswap.shiftswap.model.User;
import com.shiftswap.shiftswap.model.ShiftSwap;
import com.shiftswap.shiftswap.repository.ShiftRepository;
import com.shiftswap.shiftswap.repository.ShiftSwapRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/shiftSwap")
public class ShiftSwapController {

    private final ShiftSwapRepository shiftSwapRepository;
    private final ShiftRepository shiftRepository;

    public ShiftSwapController(ShiftSwapRepository shiftSwapRepository, ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
        this.shiftSwapRepository = shiftSwapRepository;
    }

    @PostMapping
    public ShiftSwap createShiftSwap(@RequestBody ShiftSwap shiftSwap) {
        return shiftSwapRepository.save(shiftSwap);
    }

    @GetMapping("/{id}")
    public ShiftSwap getShiftSwap(@PathVariable Long id) {
        return shiftSwapRepository.findById(id).orElseThrow();
    }

    @GetMapping("/{shiftSwap_Id}/status")
    public String getStatus(@PathVariable Long shiftSwap_Id) {
        ShiftSwap swap = shiftSwapRepository.findById(shiftSwap_Id).orElseThrow();
        return swap.getStatus();
    }

    @PostMapping("/{shiftSwap_Id}/approve")
    public ShiftSwap approve(@PathVariable Long shiftSwap_Id) {
        ShiftSwap swap = shiftSwapRepository.findById(shiftSwap_Id).orElseThrow();

        if (!"PENDING".equals(swap.getStatus())) {
            throw new RuntimeException("Swap is not PENDING");
        }

        Shift shift = swap.getShift();
        User fromUser = swap.getFromUser();
        User toUser = swap.getToUser();

        if (shift.getAssignedUser() == null || !shift.getAssignedUser().getId().equals(fromUser.getId())) {
            throw new RuntimeException("Shift is not currently assigned to fromUser");
        }

        shift.setAssignedUser(toUser);
        shiftRepository.save(shift);

        swap.setStatus("APPROVED");
        return shiftSwapRepository.save(swap);
    }
    @PostMapping("/{shiftSwap_Id}/reject")
        public ShiftSwap reject(@PathVariable Long shiftSwap_Id) {
            ShiftSwap swap = shiftSwapRepository.findById(shiftSwap_Id).orElseThrow();

            if (!"PENDING".equals(swap.getStatus())) {
                throw new RuntimeException("Swap is not PENDING");
            }

            swap.setStatus("REJECTED");
            return shiftSwapRepository.save(swap);
        }
    @GetMapping("/pending")
        public List<ShiftSwap> pending() {
            return shiftSwapRepository.findByStatus("PENDING");
        }
    @GetMapping("/outgoing/{userId}")
        public List<ShiftSwap> outgoing(@PathVariable Long userId) {
            return shiftSwapRepository.findByFromUser_Id(userId);
        }
    @GetMapping("/incoming/{userId}")
        public List<ShiftSwap> incoming(@PathVariable Long userId) {
            return shiftSwapRepository.findByToUser_Id(userId);
        }

    
}
