package com.eem.demo.repository;

import com.eem.demo.entity.Temp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRepository extends JpaRepository<Temp, Integer> {
}
