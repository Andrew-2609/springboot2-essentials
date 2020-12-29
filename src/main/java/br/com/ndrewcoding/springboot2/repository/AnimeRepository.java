package br.com.ndrewcoding.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ndrewcoding.springboot2.domain.Anime;

import java.util.List;

public interface AnimeRepository extends JpaRepository<Anime, Long> {

	List<Anime> findByName(String name);
}
