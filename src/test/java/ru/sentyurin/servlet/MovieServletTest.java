package ru.sentyurin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ru.sentyurin.service.MovieService;
import ru.sentyurin.servlet.dto.MovieOutgoingDto;

class MovieServletTest {
	
	private MovieService service;
	private MovieServlet servlet;
	private StringWriter sw;
	private PrintWriter pw;
	private HttpServletResponse resp;
	
	@BeforeEach
	void init() throws IOException {
		servlet = new MovieServlet();
		
		service = Mockito.mock(MovieService.class);
		servlet.setMovieService(service);
		
		resp = Mockito.mock(HttpServletResponse.class);
		sw = new StringWriter();
		pw = new PrintWriter(sw);
		Mockito.when(resp.getWriter()).thenReturn(pw);
	}

	@Test
	void shouldReturnCorrectData() {
		List<MovieOutgoingDto> movies = new ArrayList<>();
		movies.add(new MovieOutgoingDto().setId(1).setTitle("Reservoir Dogs")
				.setReleaseYear(1992).setDirectorName("Quentin Tarantino"));
		movies.add(new MovieOutgoingDto().setId(2).setTitle("Pulp Fiction")
				.setReleaseYear(1994).setDirectorName("Quentin Tarantino"));
		movies.add(new MovieOutgoingDto().setId(3).setTitle("Django Unchained")
				.setReleaseYear(2012).setDirectorName("Quentin Tarantino"));

		Mockito.when(service.getMovies()).thenReturn(movies);

		try {
			servlet.doGet(null, resp);
			System.out.println(sw.toString());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
