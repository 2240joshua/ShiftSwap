package com.shiftswap.shiftswap.controller;

import com.shiftswap.shiftswap.model.Shift;
import com.shiftswap.shiftswap.model.User;
import com.shiftswap.shiftswap.model.ShiftSwap;
import com.shiftswap.shiftswap.model.SwapStatus; // ✅ add this
import com.shiftswap.shiftswap.repository.ShiftRepository;
import com.shiftswap.shiftswap.repository.ShiftSwapRepository;
import com.shiftswap.shiftswap.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/shiftSwap")
public class ShiftSwapController {

    private final ShiftSwapRepository shiftSwapRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    public ShiftSwapController(ShiftSwapRepository shiftSwapRepository, ShiftRepository shiftRepository, UserRepository userRepository) {
        this.shiftRepository = shiftRepository;
        this.shiftSwapRepository = shiftSwapRepository;
        this.userRepository = userRepository;
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
    public SwapStatus getStatus(@PathVariable Long shiftSwap_Id) {
        ShiftSwap swap = shiftSwapRepository.findById(shiftSwap_Id).orElseThrow();
        return swap.getStatus();
    }

    @Transactional
    @PostMapping("/{shiftSwap_Id}/approve")
    public ShiftSwap approve(@PathVariable Long shiftSwap_Id) {
        ShiftSwap swap = shiftSwapRepository.findById(shiftSwap_Id).orElseThrow();

        if (swap.getStatus() != SwapStatus.PENDING) {
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

        swap.setStatus(SwapStatus.APPROVED);
        return shiftSwapRepository.save(swap);
    }

    @PostMapping("/{shiftSwap_Id}/reject")
    public ShiftSwap reject(@PathVariable Long shiftSwap_Id) {
        ShiftSwap swap = shiftSwapRepository.findById(shiftSwap_Id).orElseThrow();

        if (swap.getStatus() != SwapStatus.PENDING) {
            throw new RuntimeException("Swap is not PENDING");
        }

        swap.setStatus(SwapStatus.REJECTED);
        return shiftSwapRepository.save(swap);
    }

    @GetMapping("/pending")
    public List<ShiftSwap> pending() {
        return shiftSwapRepository.findByStatus(SwapStatus.PENDING);
    }

    @GetMapping("/outgoing/{userId}")
    public List<ShiftSwap> outgoing(@PathVariable Long userId) {
        return shiftSwapRepository.findByFromUser_Id(userId);
    }

    @GetMapping("/incoming/{userId}")
    public List<ShiftSwap> incoming(@PathVariable Long userId) {
        return shiftSwapRepository.findByToUser_Id(userId);
    }

    private void validateEligibility(User candidate, Shift shift) {
        boolean hasOverlap = shiftRepository.existsByAssignedUser_IdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
                candidate.getId(),
                shift.getDay(),
                shift.getStartTime(),
                shift.getEndTime()
        );

        if (hasOverlap) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already has a shift during this time"
            );
        }

        if (!candidate.getRole().equalsIgnoreCase(shift.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is not qualified for this role"
            );
        }
    }

    @PostMapping("/{id}/accept")
    public ShiftSwap acceptSwap(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        ShiftSwap swap = shiftSwapRepository.findById(id).orElseThrow();
        User candidate = userRepository.findById(userId).orElseThrow();

        validateEligibility(candidate, swap.getShift());

        swap.setToUser(candidate);
        swap.setStatus(SwapStatus.PENDING);

        return shiftSwapRepository.save(swap);
    }
}
