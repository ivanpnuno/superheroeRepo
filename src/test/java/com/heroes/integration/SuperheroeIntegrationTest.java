package com.heroes.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.heroes.commands.CreateCommand;
import com.heroes.domain.SuperheroeDTO;
import com.heroes.domain.entity.Superheroe;
import com.heroes.repository.SuperheroeRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class SuperheroeIntegrationTest {
	
	@Autowired
	private SuperheroeRepository superheroeRepository;

	@LocalServerPort
	private int port;
	
	private String baseurl;

	private static RestTemplate restTemplate;

	@BeforeAll
	public void init() {
		restTemplate = new RestTemplate();
		
		baseurl = String.format("http://localhost:%d/api/heroes", port);
		
		// Add some mocked heroes
		superheroeRepository.save(Superheroe.builder().name("Supertest").build());
		superheroeRepository.save(Superheroe.builder().name("Testman").build());
		superheroeRepository.save(Superheroe.builder().name("Mr Test").build());
	}

	@AfterAll
	void cleanupDatabase() {
		superheroeRepository.deleteAll();
	}

	@Test
	public void returnHeroesList() {
		ResponseEntity<SuperheroeDTO[]> response = restTemplate
				.getForEntity(baseurl, SuperheroeDTO[].class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// check database size
		int size = response.getBody().length;
		assertThat(superheroeRepository.findAll().size() == size);
	}

	@Test
	public void returnHeroeByIdShouldReturnHeroe() {
		ResponseEntity<SuperheroeDTO> response = restTemplate
				.getForEntity(baseurl + "/{id}", SuperheroeDTO.class, 1L);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getId()).isEqualTo(1L);
		assertThat(response.getBody().getName()).isEqualTo("Supertest");
	}

	@Test
	public void returnHeroeByIdShouldReturnNotFoundException() {
		try {
			restTemplate.getForEntity(baseurl + "/{id}", SuperheroeDTO.class,
					99L);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("404");
			assertThat(e.getMessage()).contains("Superheroe not found with id 99");
		}
	}

	@Test
	public void searchWithEmptyFilterShouldReturnNotFoundException() {
		try {
			String filter = "";
			restTemplate.getForEntity(baseurl + "/search?filter={1}", SuperheroeDTO.class, filter);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("400");
			assertThat(e.getMessage()).contains("Filter is not valid");
		}
	}

	@Test
	public void searchShouldReturnEmtyResultsForFilter() {
		String filter = "spider";
		ResponseEntity<SuperheroeDTO[]> response = restTemplate.getForEntity(baseurl + "/search?filter={1}", SuperheroeDTO[].class, filter);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().length).isEqualTo(0);
		assertThat(superheroeRepository.findAll().size() == 2);
	}
	
	@Test
	public void searchShouldReturnOneHeroe() {
		String filter = "man";
		ResponseEntity<SuperheroeDTO[]> response = restTemplate.getForEntity(baseurl + "/search?filter={1}", SuperheroeDTO[].class, filter);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().length).isEqualTo(1);
		assertThat(response.getBody()[0].getName()).isEqualTo("Testman");
		assertThat(superheroeRepository.findAll().size() == 2);
	}
	
	@Test
	public void createWithEmptyNameShouldReturnException() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    CreateCommand command = new CreateCommand();
		HttpEntity<CreateCommand> request =  new HttpEntity<CreateCommand>(command, headers);
		
		try {
			restTemplate.postForEntity(baseurl + "/create", request,SuperheroeDTO.class);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("400");
			assertThat(e.getMessage()).contains("name must not be empty");
		}
	}
	
	@Test
	public void createShouldReturnExceptionIfHeroeExists() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    CreateCommand command = new CreateCommand();
	    command.setName("Supertest");
		HttpEntity<CreateCommand> request =  new HttpEntity<CreateCommand>(command, headers);
		
		try {
			restTemplate.postForEntity(baseurl + "/create", request,SuperheroeDTO.class);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("404");
			assertThat(e.getMessage()).contains("Error creating Superheroe");
		}
	}
	
	@Test
	public void createShouldSaveHeroe() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    CreateCommand command = new CreateCommand();
	    command.setName("Spidertest");
		HttpEntity<CreateCommand> request =  new HttpEntity<CreateCommand>(command, headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity(baseurl + "/create", request,String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		response.getBody().contains("A new Superhero has born, Spidertest!");
		
		// Check database
		assertThat(!superheroeRepository.findByNameContainingIgnoreCase("Spidertest").isEmpty());
	}
	
	@Test
	public void updateWithEmptyNameShouldReturnException() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    SuperheroeDTO heroe = new SuperheroeDTO();
		HttpEntity<SuperheroeDTO> request =  new HttpEntity<SuperheroeDTO>(heroe, headers);
		
		try {
			restTemplate.postForEntity(baseurl + "/update", request,SuperheroeDTO.class);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("400");
			assertThat(e.getMessage()).contains("name must not be empty");
		}
	}
	
	@Test
	public void updateShouldReturnExceptionIfNameExists() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    SuperheroeDTO heroe = SuperheroeDTO.builder().name("Supertest").id(3L).build();
		HttpEntity<SuperheroeDTO> request =  new HttpEntity<SuperheroeDTO>(heroe, headers);
		
		try {
			restTemplate.postForEntity(baseurl + "/update", request,SuperheroeDTO.class);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("422");
			assertThat(e.getMessage()).contains("Error updating Superheroe");
		}
	}
	
	@Test
	public void updateShouldReturnExceptionIfHeroeNotExists() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    SuperheroeDTO heroe = SuperheroeDTO.builder().name("Battest").id(99L).build();
		HttpEntity<SuperheroeDTO> request =  new HttpEntity<SuperheroeDTO>(heroe, headers);
		
		try {
			restTemplate.postForEntity(baseurl + "/update", request,SuperheroeDTO.class);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("404");
			assertThat(e.getMessage()).contains("Superheroe not found with id 99");
		}
	}
	
	@Test
	public void updateShouldUpdateHeroeName() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
	    SuperheroeDTO heroe = SuperheroeDTO.builder().name("Battest").id(2L).build();
		HttpEntity<SuperheroeDTO> request =  new HttpEntity<SuperheroeDTO>(heroe, headers);
		
		ResponseEntity<SuperheroeDTO> response = restTemplate.postForEntity(baseurl + "/update", request,SuperheroeDTO.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getName()).isEqualTo("Battest");
		assertThat(response.getBody().getId()).isEqualTo(2L);
		
		// Check database
		assertThat(superheroeRepository.findById(2L).get().getName()).isEqualTo("Battest");
	}
	
	@Test
	public void deleteShouldReturnExceptionIfHeroeNotExists() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		HttpEntity<String> request =  new HttpEntity<String>("", headers);
		
		try {
			restTemplate.postForEntity(baseurl + "/delete/{1}", request,SuperheroeDTO.class, 99L);
		} catch (RestClientException e) {
			assertThat(e.getMessage()).contains("404");
			assertThat(e.getMessage()).contains("Superheroe not found with id 99");
		}
	}
	
	@Test
	public void deleteShouldDeleteHeroeName() {
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		HttpEntity<String> request =  new HttpEntity<String>("", headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity(baseurl + "/delete/{1}", request,String.class, 2L);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Superheroe removed");
		
		// Check database
		assertThat(superheroeRepository.findById(2L).isPresent()).isEqualTo(false);
	}
}
