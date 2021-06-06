package com.heroes.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.heroes.domain.SuperheroeDTO;
import com.heroes.domain.entity.Superheroe;
import com.heroes.exceptions.CustomNotFoundException;
import com.heroes.meter.SuperheroeMeter;
import com.heroes.repository.SuperheroeRepository;

@Service("superheroeService")
@CacheConfig(cacheNames="heroes")
public class SuperheroeService {

	@Autowired
	private SuperheroeRepository superheroeRepository;

	@SuperheroeMeter
	public List<SuperheroeDTO> findAll() {
		List<Superheroe> superheroes = superheroeRepository.findAll();

		return superheroes.stream().map(superheroe -> SuperheroeDTO.from(superheroe)).collect(Collectors.toList());
	}

	@SuperheroeMeter
    @Cacheable
	public SuperheroeDTO getById(Long id) throws CustomNotFoundException {
		try {
			Superheroe h = superheroeRepository.findById(id).get();
			return SuperheroeDTO.from(h);
		} catch (Exception e) {
			throw new CustomNotFoundException(String.format("Superheroe not found with id %d", id));
		}
	}

	@SuperheroeMeter
	public List<SuperheroeDTO> search(String filter) {
		List<Superheroe> superheroes = superheroeRepository.findByNameContainingIgnoreCase(filter);
		
		return superheroes.stream().map(superheroe -> SuperheroeDTO.from(superheroe)).collect(Collectors.toList());
	}

	@SuperheroeMeter
	public void create(String name) throws CustomNotFoundException {
		Superheroe superheroe = Superheroe.builder().name(name).build();

		try {
			superheroeRepository.save(superheroe);
		} catch (DataIntegrityViolationException e) {
			throw new CustomNotFoundException(String.format("Error creating Superheroe: %s", name));
		}
	}

	@SuperheroeMeter
	@CachePut(key="#result.id")
	public SuperheroeDTO update(SuperheroeDTO superheroeDTO) throws CustomNotFoundException {

		Superheroe superheroe = Superheroe.builder().id(superheroeDTO.getId()).name(superheroeDTO.getName()).build();

		if (superheroeRepository.findById(superheroe.getId()).isEmpty()) {
			throw new CustomNotFoundException(String.format("Superheroe not found with id %d", superheroe.getId()));
		}

		try {
			superheroeRepository.save(superheroe);
		} catch (DataIntegrityViolationException e) {
			throw new CustomNotFoundException(String.format("Error updating Superheroe"));
		}
		
		return superheroeDTO;
	}

	@SuperheroeMeter
	@CacheEvict(key="#id")
	public void delete(Long id) throws CustomNotFoundException {
		try {
			superheroeRepository.deleteById(id);
		} catch (Exception e) {
			throw new CustomNotFoundException(String.format("Superheroe not found with id %d", id));
		}
	}
}
