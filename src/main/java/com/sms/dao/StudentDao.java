package com.sms.dao;

import com.sms.entity.Student;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生档案 DAO
 */
public class StudentDao {

    public List<Student> findAll() {
        String sql = "SELECT s.*, cl.name AS class_name FROM `student` s " +
                "LEFT JOIN `clazz` cl ON s.class_id = cl.id ORDER BY s.student_no";
        List<Student> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Student> findByClass(Long classId) {
        String sql = "SELECT s.*, cl.name AS class_name FROM `student` s " +
                "LEFT JOIN `clazz` cl ON s.class_id = cl.id WHERE s.class_id=? ORDER BY s.student_no";
        List<Student> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Student> findByClassIds(List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) return new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT s.*, cl.name AS class_name FROM `student` s " +
                "LEFT JOIN `clazz` cl ON s.class_id = cl.id WHERE s.class_id IN (");
        for (int i = 0; i < classIds.size(); i++) sb.append(i == 0 ? "?" : ",?");
        sb.append(") ORDER BY s.class_id, s.student_no");
        List<Student> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < classIds.size(); i++) ps.setLong(i + 1, classIds.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Student findById(Long id) {
        String sql = "SELECT s.*, cl.name AS class_name FROM `student` s " +
                "LEFT JOIN `clazz` cl ON s.class_id = cl.id WHERE s.id=?";
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

    public Student findByUserId(Long userId) {
        String sql = "SELECT s.*, cl.name AS class_name FROM `student` s " +
                "LEFT JOIN `clazz` cl ON s.class_id = cl.id WHERE s.user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Student findByNo(String studentNo) {
        String sql = "SELECT s.*, cl.name AS class_name FROM `student` s " +
                "LEFT JOIN `clazz` cl ON s.class_id = cl.id WHERE s.student_no=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Long insert(Student s) {
        String sql = "INSERT INTO `student`(`user_id`,`student_no`,`name`,`gender`,`phone`,`address`,`class_id`,`enrollment_year`) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, s.getUserId());
            ps.setString(2, s.getStudentNo());
            ps.setString(3, s.getName());
            ps.setString(4, s.getGender());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getAddress());
            if (s.getClassId() == null) ps.setNull(7, Types.BIGINT);
            else ps.setLong(7, s.getClassId());
            ps.setString(8, s.getEnrollmentYear());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int update(Student s) {
        String sql = "UPDATE `student` SET `student_no`=?,`name`=?,`gender`=?,`phone`=?,`address`=?,`class_id`=?,`enrollment_year`=? WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getStudentNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getGender());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getAddress());
            if (s.getClassId() == null) ps.setNull(6, Types.BIGINT);
            else ps.setLong(6, s.getClassId());
            ps.setString(7, s.getEnrollmentYear());
            ps.setLong(8, s.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM `student` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM `student` WHERE `user_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setStudentNo(rs.getString("student_no"));
        s.setName(rs.getString("name"));
        s.setGender(rs.getString("gender"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        long cl = rs.getLong("class_id");
        s.setClassId(rs.wasNull() ? null : cl);
        s.setEnrollmentYear(rs.getString("enrollment_year"));
        s.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        s.setClassName(rs.getString("class_name"));
        return s;
    }
}
