package com.heroes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.heroes.domain.SuperheroeDTO;
import com.heroes.domain.entity.Superheroe;
import com.heroes.exceptions.BaseException;
import com.heroes.exceptions.CustomNotFoundException;
import com.heroes.repository.SuperheroeRepository;

@ExtendWith(MockitoExtension.class)
public class SuperheroeServiceTest {

	@Mock
    private SuperheroeRepository superheroeRepository;
	
	@InjectMocks
    private SuperheroeService superheroeService;
	
	@Test
    public void findAllShoulReturnHeroesListForMockedHeroes() {
    	// Mock Superhero
		List<Superheroe> heroes = new ArrayList<Superheroe>();
		heroes.add(new Superheroe(1L, "Supertest"));
		heroes.add(new Superheroe(2L, "Testman"));
		when(superheroeRepository.findAll()).thenReturn(heroes);

		List<SuperheroeDTO> list = superheroeService.findAll();
	    assertThat(list.size()).isEqualTo(heroes.size());
	    assertThat(list.get(0).getName()).isEqualTo(heroes.get(0).getName());
	    assertThat(list.get(1).getName()).isEqualTo(heroes.get(1).getName());
    }
	
	@Test
    public void findAllShoulReturnEmptyHeroesList() {
    	// Mock Superhero
		when(superheroeRepository.findAll()).thenReturn(new ArrayList<Superheroe>());

		List<SuperheroeDTO> list = superheroeService.findAll();
	    assertThat(list.isEmpty()).isEqualTo(true);
    }
	
    @Test
    public void findByIdShoulReturnHeroForMockedHero() throws CustomNotFoundException {
    	// Mock Superhero
		when(superheroeRepository.findById(1L)).thenReturn(Optional.of(new Superheroe(1L, "Supertest")));

		SuperheroeDTO response = superheroeService.getById(1L);
	    assertThat(response.getName()).isEqualTo("Supertest");
	    assertThat(response.getId()).isEqualTo(1L);
    }
    
    @Test
    public void findByIdShoulReturnError() {  	
		try {
			when(superheroeRepository.findById(1L)).thenReturn(null);
			superheroeService.getById(1L);
		} catch (BaseException e) {
			assertThat(e.getExceptionCode()).isEqualTo(404);
			assertThat(e.getMessage()).isEqualTo("Superheroe not found with id 1");
		}
    }
    
    @Test
    public void updateShoulReturnHeroForMockedHero() throws BaseException {
    	// Mock Superhero
		when(superheroeRepository.findById(1L)).thenReturn(Optional.of(new Superheroe(1L, "Testman")));

		SuperheroeDTO response = superheroeService.update(new SuperheroeDTO(1L, "Supertest"));
	    assertThat(response.getName()).isEqualTo("Supertest");
	    assertThat(response.getId()).isEqualTo(1L);
    }
    
    @Test
    public void updateShoulReturnErrorForNotExisintgHero() throws CustomNotFoundException {
    	// Mock Superhero
		when(superheroeRepository.findById(1L)).thenReturn(Optional.empty());

		try {
			superheroeService.update(new SuperheroeDTO(1L, "Supertest"));
		} catch (BaseException e) {
			assertThat(e.getExceptionCode()).isEqualTo(404);
			assertThat(e.getMessage()).isEqualTo("Superheroe not found with id 1");
		}
    }
    
    @Test
    public void updateShoulReturnErrorForExisintgHeroWithSameName() throws CustomNotFoundException {
    	// Mock Superhero
		when(superheroeRepository.findById(1L)).thenReturn(Optional.of(new Superheroe(1L, "Testman")));
		when(superheroeRepository.save(Mockito.any(Superheroe.class))).thenThrow(new DataIntegrityViolationException("test"));

		try {
			superheroeService.update(new SuperheroeDTO(1L, "Supertest"));
		} catch (BaseException e) {
			assertThat(e.getExceptionCode()).isEqualTo(422);
			assertThat(e.getMessage()).isEqualTo("Error updating Superheroe");
		}
    }
}
