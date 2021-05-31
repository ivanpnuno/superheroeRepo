package com.heroes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heroes.domain.entity.Superheroe;

@Repository
public interface SuperheroeRepository extends JpaRepository<Superheroe, Long> {
	
    List<Superheroe> findByNameContainingIgnoreCase(String filter);
}
