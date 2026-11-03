package com.sms.dao;

import com.sms.entity.Score;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩 DAO（含统计查询）
 */
public class ScoreDao {

    /** 学生本人的成绩列表（含课程名、学分） */
    public List<Score> findByStudent(Long studentId) {
        String sql = "SELECT s.*, c.name AS course_name, c.credit AS credit " +
                "FROM `score` s JOIN `course` c ON s.course_id = c.id " +
                "WHERE s.student_id=? ORDER BY c.id";
        List<Score> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Score s = map(rs);
                    s.setCourseName(rs.getString("course_name"));
                    s.setCredit(rs.getDouble("credit"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 教师录入成绩：某课程某班级下所有学生及其成绩（含未录成绩的学生） */
    public List<Score> findByCourseAndClass(Long courseId, Long classId) {
        String sql = "SELECT st.id AS student_id, st.student_no, st.name AS student_name, " +
                "sc.id AS id, sc.score AS score, sc.course_id AS course_id " +
                "FROM `student` st " +
                "LEFT JOIN `score` sc ON sc.student_id = st.id AND sc.course_id = ? " +
                "WHERE st.class_id = ? ORDER BY st.student_no";
        List<Score> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ps.setLong(2, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Score s = new Score();
                    s.setStudentId(rs.getLong("student_id"));
                    s.setStudentNo(rs.getString("student_no"));
                    s.setStudentName(rs.getString("student_name"));
                    s.setCourseId(rs.getLong("course_id"));
                    long id = rs.getLong("id");
                    s.setId(rs.wasNull() ? null : id);
                    double score = rs.getDouble("score");
                    s.setScore(rs.wasNull() ? null : score);
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 新增或更新成绩（student_id + course_id 唯一） */
    public int upsert(Score s) {
        String sql = "INSERT INTO `score`(`student_id`,`course_id`,`score`) VALUES(?,?,?) " +
                "ON DUPLICATE KEY UPDATE `score`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, s.getStudentId());
            ps.setLong(2, s.getCourseId());
            ps.setDouble(3, s.getScore());
            ps.setDouble(4, s.getScore());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteByStudent(Long studentId) {
        String sql = "DELETE FROM `score` WHERE `student_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM `score` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** 班级排名：按加权平均分（Σ(score*credit)/Σcredit）降序 */
    public List<Map<String, Object>> findClassRanking(Long classId) {
        String sql = "SELECT st.id AS student_id, st.student_no, st.name AS student_name, " +
                "ROUND(SUM(sc.score * c.credit) / NULLIF(SUM(c.credit),0), 2) AS avg_score " +
                "FROM `student` st " +
                "LEFT JOIN `score` sc ON sc.student_id = st.id " +
                "LEFT JOIN `course` c ON c.id = sc.course_id " +
                "WHERE st.class_id = ? GROUP BY st.id, st.student_no, st.name " +
                "ORDER BY avg_score DESC";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("studentId", rs.getLong("student_id"));
                    m.put("studentNo", rs.getString("student_no"));
                    m.put("studentName", rs.getString("student_name"));
                    double avg = rs.getDouble("avg_score");
                    m.put("avgScore", rs.wasNull() ? null : avg);
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 某班级全部成绩分数值（用于分布统计） */
    public List<Double> findScoreValuesByClass(Long classId) {
        String sql = "SELECT sc.score FROM `score` sc JOIN `student` st ON st.id = sc.student_id " +
                "WHERE st.class_id=? AND sc.score IS NOT NULL";
        List<Double> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getDouble("score"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 某课程全部成绩分数值（用于分布统计） */
    public List<Double> findScoreValuesByCourse(Long courseId) {
        String sql = "SELECT score FROM `score` WHERE course_id=? AND score IS NOT NULL";
        List<Double> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getDouble("score"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 各班平均分 */
    public List<Map<String, Object>> findClassAverages() {
        String sql = "SELECT cl.id, cl.name, ROUND(AVG(sc.score), 2) AS avg_score " +
                "FROM `clazz` cl LEFT JOIN `student` st ON st.class_id = cl.id " +
                "LEFT JOIN `score` sc ON sc.student_id = st.id " +
                "GROUP BY cl.id, cl.name ORDER BY cl.id";
        return queryAvg(sql);
    }

    /** 各课程平均分 */
    public List<Map<String, Object>> findCourseAverages() {
        String sql = "SELECT c.id, c.name, ROUND(AVG(sc.score), 2) AS avg_score " +
                "FROM `course` c LEFT JOIN `score` sc ON sc.course_id = c.id " +
                "GROUP BY c.id, c.name ORDER BY c.id";
        return queryAvg(sql);
    }

    private List<Map<String, Object>> queryAvg(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("name", rs.getString("name"));
                double avg = rs.getDouble("avg_score");
                m.put("avgScore", rs.wasNull() ? null : avg);
                list.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private Score map(ResultSet rs) throws SQLException {
        Score s = new Score();
        s.setId(rs.getLong("id"));
        s.setStudentId(rs.getLong("student_id"));
        s.setCourseId(rs.getLong("course_id"));
        double sc = rs.getDouble("score");
        s.setScore(rs.wasNull() ? null : sc);
        s.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        return s;
    }
}
