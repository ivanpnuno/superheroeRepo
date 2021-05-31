package com.heroes.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.heroes.domain.entity.Superheroe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuperheroeDTO {
	
	@NotNull(message = "id is required")
	private Long id;
	
	@NotNull(message = "name is required")
	@NotEmpty(message = "name must not be empty")
	private String name;
	
	public static SuperheroeDTO from(Superheroe superheroe) {
		return SuperheroeDTO.builder()
		.id(superheroe.getId())
		.name(superheroe.getName())
		.build();
	}
	
}
