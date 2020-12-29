package br.com.ndrewcoding.springboot2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import br.com.ndrewcoding.springboot2.domain.Anime;
import br.com.ndrewcoding.springboot2.requests.AnimePostRequestBody;
import br.com.ndrewcoding.springboot2.requests.AnimePutRequestBody;

@Mapper(componentModel = "spring")
@Component
public abstract class AnimeMapper {
	public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

	public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);

	public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);
}
