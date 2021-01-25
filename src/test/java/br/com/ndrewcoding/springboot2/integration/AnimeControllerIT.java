package br.com.ndrewcoding.springboot2.integration;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import br.com.ndrewcoding.springboot2.domain.Anime;
import br.com.ndrewcoding.springboot2.domain.NdrewCodingUser;
import br.com.ndrewcoding.springboot2.repository.AnimeRepository;
import br.com.ndrewcoding.springboot2.repository.NdrewCodingUserRepository;
import br.com.ndrewcoding.springboot2.requests.AnimePostRequestBody;
import br.com.ndrewcoding.springboot2.util.AnimeCreator;
import br.com.ndrewcoding.springboot2.util.AnimePostRequestBodyCreator;
import br.com.ndrewcoding.springboot2.wrapper.PageableResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {
	@Autowired
	@Qualifier(value = "testRestTemplateRoleUserRoleUser")
	private TestRestTemplate testRestTemplateRoleUser;

	@Autowired
	@Qualifier(value = "testRestTemplateRoleUserRoleAdmin")
	private TestRestTemplate testRestTemplateRoleAdmin;

	@Autowired
	private AnimeRepository animeRepository;
	@Autowired
	private NdrewCodingUserRepository ndrewCodingUserRepository;
	private static final NdrewCodingUser USER = NdrewCodingUser.builder().name("Andrew Bunro").username("bunro")
			.password("{bcrypt}$2a$10$e8xFDPx1uyg16xvNTDPnb.xLVFA8Wiu7tWsge4RlziNV/Xkp19rlS").authorities("ROLE_USER")
			.build();
	private static final NdrewCodingUser ADMIN = NdrewCodingUser.builder().name("Andrew Monteiro").username("andrew")
			.password("{bcrypt}$2a$10$e8xFDPx1uyg16xvNTDPnb.xLVFA8Wiu7tWsge4RlziNV/Xkp19rlS")
			.authorities("ROLE_USER,ROLE_ADMIN").build();

	@TestConfiguration
	@Lazy
	static class Config {
		@Bean(name = "testRestTemplateRoleUserRoleUser")
		public TestRestTemplate testRestTemplateRoleUserRoleUserCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:" + port)
					.basicAuthentication("bunro", "ndrewcoding");
			return new TestRestTemplate(restTemplateBuilder);
		}

		@Bean(name = "testRestTemplateRoleUserRoleAdmin")
		public TestRestTemplate testRestTemplateRoleUserRoleAdminCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:" + port)
					.basicAuthentication("andrew", "ndrewcoding");
			return new TestRestTemplate(restTemplateBuilder);
		}
	}

	@Test
	@DisplayName("list returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(USER);

		String expectedName = savedAnime.getName();

		PageableResponse<Anime> animePage = testRestTemplateRoleUser
				.exchange("/animes", HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Anime>>() {
				}).getBody();

		Assertions.assertThat(animePage).isNotNull();

		Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("listAll returns list of anime when successful")
	void listAll_ReturnsListOfAnimes_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(USER);

		String expectedName = savedAnime.getName();

		List<Anime> animes = testRestTemplateRoleUser
				.exchange("/animes/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnsAnime_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(USER);

		Long expectedId = savedAnime.getId();

		Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

		Assertions.assertThat(anime).isNotNull();

		Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
	}

	@Test
	@DisplayName("findByName returns a list of anime when successful")
	void findByName_ReturnsListOfAnime_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(USER);

		String expectedName = savedAnime.getName();

		String url = String.format("/animes/find?name=%s", expectedName);

		List<Anime> animes = testRestTemplateRoleUser
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("findByName returns an empty list of anime when anime is not found")
	void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
		ndrewCodingUserRepository.save(USER);

		List<Anime> animes = testRestTemplateRoleUser
				.exchange("/animes/find?name=dbz", HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
				}).getBody();

		Assertions.assertThat(animes).isNotNull().isEmpty();

	}

	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnsAnime_WhenSuccessful() {
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

		ndrewCodingUserRepository.save(ADMIN);

		ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleAdmin.postForEntity("/animes/admin",
				animePostRequestBody, Anime.class);

		Assertions.assertThat(animeResponseEntity).isNotNull();

		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();

		Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
	}

	@Test
	@DisplayName("save returns 403 when user is not admin")
	void save_Returns_403_WhenUserIsNotAdmin() {
		AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

		ndrewCodingUserRepository.save(USER);

		ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes/admin",
				animePostRequestBody, Anime.class);

		Assertions.assertThat(animeResponseEntity).isNotNull();

		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

		Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();

		Assertions.assertThat(animeResponseEntity.getBody().getId()).isNull();
	}

	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(ADMIN);

		savedAnime.setName("new name");

		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin", HttpMethod.PUT,
				new HttpEntity<>(savedAnime), Void.class);

		Assertions.assertThat(animeResponseEntity).isNotNull();

		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DisplayName("replace returns 403 when user is not admin")
	void replace_Returns_403_WhenUserIsNotAdmin() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(USER);

		savedAnime.setName("new name");

		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin", HttpMethod.PUT,
				new HttpEntity<>(savedAnime), Void.class);

		Assertions.assertThat(animeResponseEntity).isNotNull();

		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(ADMIN);

		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
				HttpMethod.DELETE, null, Void.class, savedAnime.getId());

		Assertions.assertThat(animeResponseEntity).isNotNull();

		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DisplayName("delete returns 403 when user is not admin")
	void delete_Returns_403_WhenUserIsNotAdmin() {
		Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

		ndrewCodingUserRepository.save(USER);

		ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}",
				HttpMethod.DELETE, null, Void.class, savedAnime.getId());

		Assertions.assertThat(animeResponseEntity).isNotNull();

		Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

}
