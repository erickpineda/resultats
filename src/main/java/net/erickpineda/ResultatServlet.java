package net.erickpineda;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class ResultatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Context envContext = null;
	private Context initContext = null;
	private DataSource ds = null;
	private Connection con = null;

	public ResultatServlet() {
	}

	private void contecta() throws NamingException, SQLException {
		envContext = new InitialContext();
		initContext = (Context) envContext.lookup("java:/comp/env");
		ds = (DataSource) initContext.lookup("jdbc/futbol");
		con = ds.getConnection();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int slash = request.getRequestURI().lastIndexOf("/");
		String equipo = request.getRequestURI().substring(slash + 1);
		String equipoUTF8 = java.net.URLDecoder.decode(equipo.trim(), "UTF-8");

		try {
			contecta();
			mostrarEquipos(
					response,
					"SELECT e.nom, r.temporada, r.punts, r.posicio, r.guanyats, "
							+ "r.empatats, r.perduts, r.golsf, r.golsc FROM resultats r "
							+ "inner join equips e on (r.equip = e.id) where e.nom = '"
							+ equipoUTF8 + "'");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void mostrarEquipos(HttpServletResponse response, String query)
			throws SQLException, IOException {

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.print("<html><title>Histórico Liga</title><body>");
		out.print("<center><h1>Detalles Resultados</h1></center>");
		out.print("<table border=\"1\" cellspacing=10 cellpadding=5>");
		out.print("<tr><th>Posicio</th>");
		out.print("<th>Nom</th>");
		out.print("<th>Temporada</th>");
		out.print("<th>Punts</th>");
		out.print("<th>Perduts</th>");
		out.print("<th>Guanyats</th>");
		out.print("<th>Empatats</th>");
		out.print("<th>Gols a favor</th>");
		out.print("<th>Gols en contra</th></tr>");

		while (rs.next()) {
			out.print("<tr>");
			out.print("<td>" + rs.getInt("posicio") + "</td>");
			out.print("<td>" + rs.getString("nom") + "</td>");
			out.print("<td>" + rs.getInt("temporada") + "</td>");
			out.print("<td>" + rs.getInt("punts") + "</td>");
			out.print("<td>" + rs.getInt("perduts") + "</td>");
			out.print("<td>" + rs.getInt("guanyats") + "</td>");
			out.print("<td>" + rs.getInt("empatats") + "</td>");
			out.print("<td>" + rs.getInt("golsf") + "</td>");
			out.print("<td>" + rs.getInt("golsc") + "</td>");
			out.print("</tr>");
		}
		out.print("</table></body></html>");
	}
}
