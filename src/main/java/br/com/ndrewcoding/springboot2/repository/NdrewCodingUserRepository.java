package br.com.ndrewcoding.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ndrewcoding.springboot2.domain.NdrewCodingUser;

public interface NdrewCodingUserRepository extends JpaRepository<NdrewCodingUser, Long> {
	NdrewCodingUser findByUsername(String username);
}
