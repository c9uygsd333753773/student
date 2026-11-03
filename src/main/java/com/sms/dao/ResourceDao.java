package com.sms.dao;

import com.sms.entity.Resource;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教学资源（课件）DAO
 */
public class ResourceDao {

    public List<Resource> findAll() {
        String sql = "SELECT r.*, t.name AS teacher_name, c.name AS course_name FROM `resource` r " +
                "LEFT JOIN `teacher` t ON r.teacher_id = t.id " +
                "LEFT JOIN `course` c ON r.course_id = c.id ORDER BY r.created_at DESC";
        List<Resource> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Resource> findByTeacher(Long teacherId) {
        String sql = "SELECT r.*, t.name AS teacher_name, c.name AS course_name FROM `resource` r " +
                "LEFT JOIN `teacher` t ON r.teacher_id = t.id " +
                "LEFT JOIN `course` c ON r.course_id = c.id WHERE r.teacher_id=? ORDER BY r.created_at DESC";
        List<Resource> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Resource findById(Long id) {
        String sql = "SELECT r.*, t.name AS teacher_name, c.name AS course_name FROM `resource` r " +
                "LEFT JOIN `teacher` t ON r.teacher_id = t.id " +
                "LEFT JOIN `course` c ON r.course_id = c.id WHERE r.id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Long insert(Resource r) {
        String sql = "INSERT INTO `resource`(`teacher_id`,`course_id`,`filename`,`stored_name`,`path`,`description`) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (r.getTeacherId() == null) ps.setNull(1, Types.BIGINT); else ps.setLong(1, r.getTeacherId());
            if (r.getCourseId() == null) ps.setNull(2, Types.BIGINT); else ps.setLong(2, r.getCourseId());
            ps.setString(3, r.getFilename());
            ps.setString(4, r.getStoredName());
            ps.setString(5, r.getPath());
            ps.setString(6, r.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int delete(Long id) {
        String sql = "DELETE FROM `resource` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Resource map(ResultSet rs) throws SQLException {
        Resource r = new Resource();
        r.setId(rs.getLong("id"));
        long t = rs.getLong("teacher_id");
        r.setTeacherId(rs.wasNull() ? null : t);
        long co = rs.getLong("course_id");
        r.setCourseId(rs.wasNull() ? null : co);
        r.setFilename(rs.getString("filename"));
        r.setStoredName(rs.getString("stored_name"));
        r.setPath(rs.getString("path"));
        r.setDescription(rs.getString("description"));
        r.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        r.setTeacherName(rs.getString("teacher_name"));
        r.setCourseName(rs.getString("course_name"));
        return r;
    }
}
