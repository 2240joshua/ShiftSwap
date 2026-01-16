package com.shiftswap.shiftswap.repository;
import com.shiftswap.shiftswap.model.Shift;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;



public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByAssignedUser_Id(Long userId);
}
