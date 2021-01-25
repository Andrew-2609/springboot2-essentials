package br.com.ndrewcoding.springboot2.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.ndrewcoding.springboot2.repository.NdrewCodingUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NdrewCodingUserDetailsService implements UserDetailsService {
	private final NdrewCodingUserRepository ndrewCodingUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		return Optional.ofNullable(ndrewCodingUserRepository.findByUsername(username))
				.orElseThrow(() -> new UsernameNotFoundException("NdrewCoding User not found"));
	}

}
