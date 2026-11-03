package com.sms.dao;

import com.sms.entity.Course;
import com.sms.entity.CourseClass;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程 DAO（含课程-班级关联）
 */
public class CourseDao {

    public List<Course> findAll() {
        String sql = "SELECT c.*, t.name AS teacher_name FROM `course` c " +
                "LEFT JOIN `teacher` t ON c.teacher_id = t.id ORDER BY c.id";
        List<Course> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Course> findByTeacher(Long teacherId) {
        String sql = "SELECT c.*, t.name AS teacher_name FROM `course` c " +
                "LEFT JOIN `teacher` t ON c.teacher_id = t.id WHERE c.teacher_id=? ORDER BY c.id";
        List<Course> list = new ArrayList<>();
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

    public List<Course> findByClass(Long classId) {
        String sql = "SELECT c.*, t.name AS teacher_name FROM `course` c " +
                "JOIN `course_class` cc ON cc.course_id = c.id " +
                "LEFT JOIN `teacher` t ON c.teacher_id = t.id " +
                "WHERE cc.class_id=? ORDER BY c.id";
        List<Course> list = new ArrayList<>();
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

    public Course findById(Long id) {
        String sql = "SELECT c.*, t.name AS teacher_name FROM `course` c " +
                "LEFT JOIN `teacher` t ON c.teacher_id = t.id WHERE c.id=?";
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

    public Long insert(Course course) {
        String sql = "INSERT INTO `course`(`name`,`credit`,`teacher_id`) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getName());
            ps.setDouble(2, course.getCredit() == null ? 0 : course.getCredit());
            if (course.getTeacherId() == null) ps.setNull(3, Types.BIGINT);
            else ps.setLong(3, course.getTeacherId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int update(Course course) {
        String sql = "UPDATE `course` SET `name`=?,`credit`=?,`teacher_id`=? WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, course.getName());
            ps.setDouble(2, course.getCredit() == null ? 0 : course.getCredit());
            if (course.getTeacherId() == null) ps.setNull(3, Types.BIGINT);
            else ps.setLong(3, course.getTeacherId());
            ps.setLong(4, course.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        try (Connection c = DBUtil.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM `course_class` WHERE `course_id`=?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM `course` WHERE `id`=?")) {
                ps.setLong(1, id);
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== 课程-班级关联 =====

    public List<CourseClass> listClassesByCourse(Long courseId) {
        String sql = "SELECT cc.*, cl.name AS class_name FROM `course_class` cc " +
                "JOIN `clazz` cl ON cc.class_id = cl.id WHERE cc.course_id=? ORDER BY cl.id";
        List<CourseClass> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseClass cc = new CourseClass();
                    cc.setId(rs.getLong("id"));
                    cc.setCourseId(rs.getLong("course_id"));
                    cc.setClassId(rs.getLong("class_id"));
                    cc.setClassName(rs.getString("class_name"));
                    list.add(cc);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public int bindClass(Long courseId, Long classId) {
        String sql = "INSERT IGNORE INTO `course_class`(`course_id`,`class_id`) VALUES(?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ps.setLong(2, classId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int unbindClass(Long courseId, Long classId) {
        String sql = "DELETE FROM `course_class` WHERE `course_id`=? AND `class_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ps.setLong(2, classId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 某教师授课/带班涉及的全部班级 id */
    public List<Long> findClassIdsByTeacher(Long teacherId) {
        String sql = "SELECT DISTINCT cl.id FROM `clazz` cl " +
                "LEFT JOIN `course_class` cc ON cc.class_id = cl.id " +
                "LEFT JOIN `course` c ON cc.course_id = c.id " +
                "WHERE c.teacher_id = ? OR cl.head_teacher_id = ? ORDER BY cl.id";
        List<Long> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ps.setLong(2, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 清空教师名下的课程（删除教师时级联） */
    public int clearTeacher(Long teacherId) {
        String sql = "UPDATE `course` SET `teacher_id`=NULL WHERE `teacher_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setName(rs.getString("name"));
        course.setCredit(rs.getDouble("credit"));
        long t = rs.getLong("teacher_id");
        course.setTeacherId(rs.wasNull() ? null : t);
        course.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        course.setTeacherName(rs.getString("teacher_name"));
        return course;
    }
}
