package com.heroes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import com.heroes.commands.CreateCommand;
import com.heroes.domain.SuperheroeDTO;
import com.heroes.exceptions.BaseException;
import com.heroes.exceptions.CustomBadRequestException;
import com.heroes.exceptions.CustomNotFoundException;
import com.heroes.meter.SuperheroeMeter;
import com.heroes.service.SuperheroeService;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

@RestController
@RequestMapping("/api")
@ApplicationScope
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Validated
public class HeroController {
	
	@Autowired
	private SuperheroeService superheroeService;
	
	@GetMapping("/heroes")
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public List<SuperheroeDTO> heroes() {
		return superheroeService.findAll();
	}
	
	@GetMapping("/heroes/{id}")
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public SuperheroeDTO heroe(@PathVariable("id") Long id) throws CustomNotFoundException {
		return superheroeService.getById(id);
	}
	
	@GetMapping("/heroes/search")
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	@Validated
	public List<SuperheroeDTO> search(@RequestParam(value = "filter") String filter) throws BaseException {
		if(filter.isEmpty()) {
			throw new CustomBadRequestException(String.format("Filter is not valid"));
		}
		return superheroeService.search(filter);
	}
	
	@PostMapping("/heroes/create")
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public String create(@Valid @RequestBody CreateCommand create, Errors errors) throws BaseException {
		superheroeService.create(create.getName());
		return String.format("A new Superhero has born, %s!", create.getName());
	}
	
	@PostMapping("/heroes/update")
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public SuperheroeDTO update(@Valid @RequestBody SuperheroeDTO superheroeDTO, Errors errors) throws BaseException {
		return superheroeService.update(superheroeDTO);
	}
	
	@PostMapping("/heroes/delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public String delete(@PathVariable("id") Long id) throws BaseException {
		superheroeService.delete(id);
		return "Superheroe removed";
	}

}
