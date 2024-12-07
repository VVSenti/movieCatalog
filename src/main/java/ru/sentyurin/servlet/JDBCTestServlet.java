package ru.sentyurin.servlet;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgresql.Driver;

import ru.sentyurin.config.ConfigLoader;
import ru.sentyurin.db.ConnectionToDbManager;
import ru.sentyurin.model.Director;
import ru.sentyurin.model.Movie;

/**
 * Servlet implementation class JDBCTestServlet
 */
@WebServlet("/JDBCTestServlet")
public class JDBCTestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String GET_ALL_MOVIES_SQL = "select m.id as id, "
			+ "m.title as title, m.release_year as release_year, "
			+ "d.id as director_id, d.name as director_name "
			+ "from Movie as m left join Director as d on d.id = m.director_id";

	private ConnectionToDbManager connetionToDbManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JDBCTestServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		connetionToDbManager = new ConnectionToDbManager();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<Movie> movies = new ArrayList<>();
		try (Connection connection = connetionToDbManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(GET_ALL_MOVIES_SQL);
				ResultSet result = statement.executeQuery()) {
			while (result.next()) {
				Movie movie = new Movie();
				movie.setId(result.getInt("id"));
				movie.setTitle(result.getString("title"));
				movie.setReleaseYear(result.getInt("release_year"));
				Director director = new Director();
				director.setName(result.getString("director_name"));
				movie.setDirector(director);
				movies.add(movie);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PrintWriter writer = response.getWriter();
		for (Movie movie : movies) {
			writer.println(movie.toString());
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
