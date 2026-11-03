package com.sms.dao;

import com.sms.entity.Clazz;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级 DAO
 */
public class ClazzDao {

    public List<Clazz> findAll() {
        String sql = "SELECT cl.*, t.name AS head_teacher_name, " +
                "(SELECT COUNT(*) FROM `student` s WHERE s.class_id = cl.id) AS student_count " +
                "FROM `clazz` cl LEFT JOIN `teacher` t ON cl.head_teacher_id = t.id ORDER BY cl.id";
        List<Clazz> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Clazz findById(Long id) {
        String sql = "SELECT cl.*, t.name AS head_teacher_name FROM `clazz` cl " +
                "LEFT JOIN `teacher` t ON cl.head_teacher_id = t.id WHERE cl.id=?";
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

    public Long insert(Clazz cl) {
        String sql = "INSERT INTO `clazz`(`name`,`grade`,`head_teacher_id`) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cl.getName());
            ps.setString(2, cl.getGrade());
            if (cl.getHeadTeacherId() == null) ps.setNull(3, Types.BIGINT);
            else ps.setLong(3, cl.getHeadTeacherId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int update(Clazz cl) {
        String sql = "UPDATE `clazz` SET `name`=?,`grade`=?,`head_teacher_id`=? WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cl.getName());
            ps.setString(2, cl.getGrade());
            if (cl.getHeadTeacherId() == null) ps.setNull(3, Types.BIGINT);
            else ps.setLong(3, cl.getHeadTeacherId());
            ps.setLong(4, cl.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM `clazz` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 教师视角：自己授课或担任班主任的班级 */
    public List<Clazz> findByTeacher(Long teacherId) {
        String sql = "SELECT DISTINCT cl.*, t.name AS head_teacher_name, " +
                "(SELECT COUNT(*) FROM `student` s WHERE s.class_id = cl.id) AS student_count " +
                "FROM `clazz` cl LEFT JOIN `teacher` t ON cl.head_teacher_id = t.id " +
                "LEFT JOIN `course_class` cc ON cc.class_id = cl.id " +
                "LEFT JOIN `course` c ON cc.course_id = c.id " +
                "WHERE c.teacher_id = ? OR cl.head_teacher_id = ? ORDER BY cl.id";
        List<Clazz> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ps.setLong(2, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 清空班主任（删除教师时级联） */
    public int clearHeadTeacher(Long teacherId) {
        String sql = "UPDATE `clazz` SET `head_teacher_id`=NULL WHERE `head_teacher_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Clazz map(ResultSet rs) throws SQLException {
        Clazz cl = new Clazz();
        cl.setId(rs.getLong("id"));
        cl.setName(rs.getString("name"));
        cl.setGrade(rs.getString("grade"));
        long ht = rs.getLong("head_teacher_id");
        cl.setHeadTeacherId(rs.wasNull() ? null : ht);
        cl.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        cl.setHeadTeacherName(rs.getString("head_teacher_name"));
        try {
            cl.setStudentCount(rs.getInt("student_count"));
        } catch (SQLException ignored) {
            cl.setStudentCount(null);
        }
        return cl;
    }
}
