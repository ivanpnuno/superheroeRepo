package com.heroes.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.heroes.service.SuperheroeService;
import com.heroes.commands.CreateCommand;
import com.heroes.domain.SuperheroeDTO;
import com.heroes.exceptions.BaseException;
import com.heroes.exceptions.CustomNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SuperheroeControllerTest {

	@Mock
    private SuperheroeService superheroeService;
	
	@InjectMocks
    private HeroController heroController;
	
	@SuppressWarnings("unchecked")
	@Test
    public void heroesShoulReturnHeroesListForMockedHeroes() throws CustomNotFoundException {
    	// Mock Superhero
		List<SuperheroeDTO> heroes = new ArrayList<SuperheroeDTO>();
		heroes.add(new SuperheroeDTO(1L, "Supertest"));
		heroes.add(new SuperheroeDTO(2L, "Testman"));
		when(superheroeService.findAll()).thenReturn(heroes);

    	Response response = heroController.heroes();
	    List<SuperheroeDTO> list = (List<SuperheroeDTO>)response.getEntity();
	    assertThat(list.size()).isEqualTo(2);
	    assertThat(list.get(0).getName()).isEqualTo("Supertest");
	    assertThat(list.get(1).getName()).isEqualTo("Testman");
	    assertThat(response.getStatus()).isEqualTo(200);
    }
	
	@SuppressWarnings("unchecked")
	@Test
    public void heroesShoulReturnEmptyHeroesList() throws CustomNotFoundException {
    	// Mock Superhero
		when(superheroeService.findAll()).thenReturn(new ArrayList<SuperheroeDTO>());

    	Response response = heroController.heroes();
	    List<SuperheroeDTO> list = (List<SuperheroeDTO>)response.getEntity();
	    assertThat(list.isEmpty()).isEqualTo(true);
	    assertThat(response.getStatus()).isEqualTo(200);
    }
	
    @Test
    public void heroeShoulReturnHeroForMockedHero() throws CustomNotFoundException {
    	// Mock Superhero
		when(superheroeService.getById(1L)).thenReturn(new SuperheroeDTO(1L, "Supertest"));

    	Response response = heroController.heroe(1L);
	    SuperheroeDTO heroe = (SuperheroeDTO)response.getEntity();
	    assertThat(heroe.getName()).isEqualTo("Supertest");
	    assertThat(response.getStatus()).isEqualTo(200);
    }
    
    @Test
    public void heroeShoulReturnError() {  	
		try {
			when(superheroeService.getById(1L)).thenThrow(new CustomNotFoundException("Error get"));
			 heroController.heroe(1L);
		} catch (BaseException e) {
			assertThat(e.getExceptionCode()).isEqualTo(404);
			assertThat(e.getMessage()).isEqualTo("Error get");
		}
    }
    
    @Test
    public void searchShoulReturnErrorForNonValidFilter() throws CustomNotFoundException {
		try {
			heroController.search(null);
		} catch (BaseException e) {
			assertThat(e.getExceptionCode()).isEqualTo(400);
			assertThat(e.getMessage()).isEqualTo("Filter is not valid");
		}
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void searchShoulReturnEmptyHeroesList() throws BaseException {
    	Response response = heroController.search("name");
	    List<SuperheroeDTO> list = (List<SuperheroeDTO>)response.getEntity();
	    assertThat(list.isEmpty()).isEqualTo(true);
	    assertThat(response.getStatus()).isEqualTo(200);
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void searchShoulReturnHeroesList() throws BaseException {
    	// Mock Superhero
    	List<SuperheroeDTO> heroes = new ArrayList<SuperheroeDTO>();
    	heroes.add(new SuperheroeDTO(1L, "Testman"));
    	when(superheroeService.search("man")).thenReturn(heroes);
    			
    	Response response = heroController.search("man");
	    List<SuperheroeDTO> list = (List<SuperheroeDTO>)response.getEntity();
	    assertThat(list.size()).isEqualTo(1);
	    assertThat(list.get(0).getName()).isEqualTo("Testman");
	    assertThat(response.getStatus()).isEqualTo(200);
    }
    
	@Test
    public void createShoulCreateNewHeroe() throws BaseException {
    	CreateCommand createCommand = new CreateCommand();
    	createCommand.setName("Supertest");
    	    			
    	Response response = heroController.create(createCommand);
	    assertThat(response.getStatus()).isEqualTo(200);
    }
}
