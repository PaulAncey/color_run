package fr.esgi.color_run.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.config.DatabaseConfig;
import fr.esgi.color_run.dao.CourseDAO;
import fr.esgi.color_run.dao.DAOException;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public Course create(Course course) throws DAOException {
        String sql = "INSERT INTO Course (nom, description, dateCourse, heureDebut, ville, adresse, " +
                "latitude, longitude, distanceKm, nbMaxParticipants, prix, avecObstacles, " +
                "causeSoutenue, idOrganisateur) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, course.getNom());
            stmt.setString(2, course.getDescription());
            stmt.setDate(3, course.getDateCourse());
            stmt.setTime(4, course.getHeureDebut());
            stmt.setString(5, course.getVille());
            stmt.setString(6, course.getAdresse());
            stmt.setFloat(7, course.getLatitude());
            stmt.setFloat(8, course.getLongitude());
            stmt.setFloat(9, course.getDistanceKm());
            stmt.setInt(10, course.getNbMaxParticipants());
            stmt.setFloat(11, course.getPrix());
            stmt.setBoolean(12, course.isAvecObstacles());
            stmt.setString(13, course.getCauseSoutenue());
            stmt.setInt(14, course.getIdOrganisateur());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La création de la course a échoué, aucune ligne affectée.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                course.setIdCourse(rs.getInt(1));
                course.setDateCreation(rs.getTimestamp(2));
            } else {
                throw new DAOException("La création de la course a échoué, aucun ID obtenu.");
            }
            
            return course;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la création de la course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Optional<Course> findById(Integer id) throws DAOException {
        String sql = "SELECT * FROM Course WHERE idCourse = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToCourse(rs));
            } else {
                return Optional.empty();
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche de la course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Course> findAll() throws DAOException {
        String sql = "SELECT * FROM Course";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
            
            return courses;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la récupération de toutes les courses: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public Course update(Course course) throws DAOException {
        String sql = "UPDATE Course SET nom = ?, description = ?, dateCourse = ?, heureDebut = ?, " +
                "ville = ?, adresse = ?, latitude = ?, longitude = ?, distanceKm = ?, " +
                "nbMaxParticipants = ?, prix = ?, avecObstacles = ?, causeSoutenue = ? " +
                "WHERE idCourse = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, course.getNom());
            stmt.setString(2, course.getDescription());
            stmt.setDate(3, course.getDateCourse());
            stmt.setTime(4, course.getHeureDebut());
            stmt.setString(5, course.getVille());
            stmt.setString(6, course.getAdresse());
            stmt.setFloat(7, course.getLatitude());
            stmt.setFloat(8, course.getLongitude());
            stmt.setFloat(9, course.getDistanceKm());
            stmt.setInt(10, course.getNbMaxParticipants());
            stmt.setFloat(11, course.getPrix());
            stmt.setBoolean(12, course.isAvecObstacles());
            stmt.setString(13, course.getCauseSoutenue());
            stmt.setInt(14, course.getIdCourse());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La mise à jour de la course a échoué, aucune course trouvée avec l'ID: " 
                        + course.getIdCourse());
            }
            
            return course;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la mise à jour de la course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public void delete(Integer id) throws DAOException {
        String sql = "DELETE FROM Course WHERE idCourse = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("La suppression de la course a échoué, aucune course trouvée avec l'ID: " + id);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la suppression de la course: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(stmt, conn);
        }
    }

    @Override
    public List<Course> findByOrganisateur(int idOrganisateur) throws DAOException {
        String sql = "SELECT * FROM Course WHERE idOrganisateur = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idOrganisateur);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
            
            return courses;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des courses par organisateur: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Course> findUpcomingCourses() throws DAOException {
        String sql = "SELECT * FROM Course WHERE dateCourse >= CURRENT_DATE ORDER BY dateCourse ASC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
            
            return courses;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des courses à venir: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Course> findByVille(String ville) throws DAOException {
        String sql = "SELECT * FROM Course WHERE LOWER(ville) LIKE LOWER(?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + ville + "%");
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
            
            return courses;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des courses par ville: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }

    @Override
    public List<Course> findWithFilters(String ville, Float minDistance, Float maxDistance, 
                                       Float maxPrix, Boolean avecObstacles) throws DAOException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM Course WHERE dateCourse >= CURRENT_DATE");
        List<Object> params = new ArrayList<>();
        
        if (ville != null && !ville.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(ville) LIKE LOWER(?)");
            params.add("%" + ville + "%");
        }
        
        if (minDistance != null) {
            sqlBuilder.append(" AND distanceKm >= ?");
            params.add(minDistance);
        }
        
        if (maxDistance != null) {
            sqlBuilder.append(" AND distanceKm <= ?");
            params.add(maxDistance);
        }
        
        if (maxPrix != null) {
            sqlBuilder.append(" AND prix <= ?");
            params.add(maxPrix);
        }
        
        if (avecObstacles != null) {
            sqlBuilder.append(" AND avecObstacles = ?");
            params.add(avecObstacles);
        }
        
        sqlBuilder.append(" ORDER BY dateCourse ASC");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        
        try {
            conn = DatabaseConfig.getConnection();
            stmt = conn.prepareStatement(sqlBuilder.toString());
            
            // Définir les paramètres de la requête
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
            
            return courses;
            
        } catch (SQLException e) {
            throw new DAOException("Erreur lors de la recherche des courses avec filtres: " + e.getMessage(), e);
        } finally {
            DatabaseConfig.closeResources(rs, stmt, conn);
        }
    }
    
    /**
     * Transforme une ligne de résultat en objet Course
     */
    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        return Course.builder()
                .idCourse(rs.getInt("idCourse"))
                .nom(rs.getString("nom"))
                .description(rs.getString("description"))
                .dateCourse(rs.getDate("dateCourse"))
                .heureDebut(rs.getTime("heureDebut"))
                .ville(rs.getString("ville"))
                .adresse(rs.getString("adresse"))
                .latitude(rs.getFloat("latitude"))
                .longitude(rs.getFloat("longitude"))
                .distanceKm(rs.getFloat("distanceKm"))
                .nbMaxParticipants(rs.getInt("nbMaxParticipants"))
                .prix(rs.getFloat("prix"))
                .avecObstacles(rs.getBoolean("avecObstacles"))
                .causeSoutenue(rs.getString("causeSoutenue"))
                .idOrganisateur(rs.getInt("idOrganisateur"))
                .dateCreation(rs.getTimestamp("dateCreation"))
                .build();
    }
}