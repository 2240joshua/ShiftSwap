package com.shiftswap.shiftswap.repository;
import com.shiftswap.shiftswap.model.ShiftSwap;
import java.util.List;
import com.shiftswap.shiftswap.model.SwapStatus;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ShiftSwapRepository extends JpaRepository<ShiftSwap, Long> {
    List<ShiftSwap> findByStatus(SwapStatus status);
    List<ShiftSwap> findByFromUser_Id(Long userId);
    List<ShiftSwap> findByToUser_Id(Long userId);
}
