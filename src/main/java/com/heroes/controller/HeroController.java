package com.heroes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/v1")
@ApplicationScope
@Produces(MediaType.APPLICATION_JSON_VALUE)
public class HeroController {
	
	@Autowired
	private SuperheroeService superheroeService;

	@GET
	@Path( "/heroes")
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public Response heroes() {
		List<SuperheroeDTO> heroes = superheroeService.findAll();
		return Response.ok(heroes).build();
	}
	
	@GET
	@Path( "/heroes/{id}")
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public Response heroe(@PathParam("id") Long id) throws CustomNotFoundException {
		SuperheroeDTO h = superheroeService.getById(id);
		return Response.ok(h).build();
	}
	
	@GET
	@Path( "/heroes/search")
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public Response search(@QueryParam("filter") String filter) throws BaseException {
		if(filter==null) {
			throw new CustomBadRequestException(String.format("Filter is not valid"));
		}
		List<SuperheroeDTO> heroes = superheroeService.search(filter);
		return Response.ok(heroes).build();
	}
	
	@POST
	@Path( "/heroes/create")
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public Response create(@Valid CreateCommand create) throws BaseException {
		superheroeService.create(create.getName());
		return Response.ok(String.format("A new Superhero has born, %s!", create.getName())).build();
	}
	
	@POST
	@Path( "/heroes/update")
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public Response update(@Valid @RequestBody SuperheroeDTO superheroeDTO) throws BaseException {
		superheroeService.update(superheroeDTO);
		return Response.ok(superheroeDTO).build();
	}
	
	@POST
	@Path( "/heroes/delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	@Produces(MediaType.APPLICATION_JSON_VALUE)
	@SuperheroeMeter
	public Response delete(@PathParam("id") Long id) throws BaseException {
		superheroeService.delete(id);
		return Response.ok("Superheroe removed").build();
	}

}
