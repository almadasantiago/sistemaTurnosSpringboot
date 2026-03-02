package com.barberia.barberservices;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarberServiceRepository extends JpaRepository<BarberService, Long> {
    
}