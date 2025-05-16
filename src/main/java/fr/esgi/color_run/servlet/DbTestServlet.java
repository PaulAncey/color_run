package fr.esgi.color_run.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import fr.esgi.color_run.database.DatabaseConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/db-test")
public class DbTestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        try (Connection conn = DatabaseConfig.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM role")) {

            resp.getWriter().println("<h1>H2 Database Test</h1>");
            resp.getWriter().println("<h2>Roles:</h2>");
            resp.getWriter().println("<ul>");

            while (rs.next()) {
                resp.getWriter()
                        .println("<li>" + rs.getString("nom_role") + " - " + rs.getString("description") + "</li>");
            }

            resp.getWriter().println("</ul>");

        } catch (Exception e) {
            resp.getWriter().println("<h1>Error</h1>");
            resp.getWriter().println("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace(resp.getWriter());
        }
    }
}