package com.heroes.commands;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommand {

	@NotNull(message = "name is required")
	@NotEmpty(message = "name must not be empty")
	private String name;

}
