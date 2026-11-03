package com.sms.dao;

import com.sms.entity.Feedback;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 反馈留言 DAO
 */
public class FeedbackDao {

    public List<Feedback> findAll() {
        String sql = "SELECT f.*, s.name AS student_name, s.student_no AS student_no " +
                "FROM `feedback` f LEFT JOIN `student` s ON f.student_id = s.id ORDER BY f.created_at DESC";
        List<Feedback> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Feedback> findByStudent(Long studentId) {
        String sql = "SELECT f.*, s.name AS student_name, s.student_no AS student_no " +
                "FROM `feedback` f LEFT JOIN `student` s ON f.student_id = s.id " +
                "WHERE f.student_id=? ORDER BY f.created_at DESC";
        List<Feedback> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Long insert(Feedback f) {
        String sql = "INSERT INTO `feedback`(`student_id`,`content`) VALUES(?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, f.getStudentId());
            ps.setString(2, f.getContent());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int reply(Long id, String reply) {
        String sql = "UPDATE `feedback` SET `reply`=?, `status`='REPLIED', `reply_time`=NOW() WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reply);
            ps.setLong(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteByStudent(Long studentId) {
        String sql = "DELETE FROM `feedback` WHERE `student_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM `feedback` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Feedback map(ResultSet rs) throws SQLException {
        Feedback f = new Feedback();
        f.setId(rs.getLong("id"));
        f.setStudentId(rs.getLong("student_id"));
        f.setContent(rs.getString("content"));
        f.setReply(rs.getString("reply"));
        f.setStatus(rs.getString("status"));
        f.setReplyTime(DBUtil.fmt(rs.getTimestamp("reply_time")));
        f.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        f.setStudentName(rs.getString("student_name"));
        f.setStudentNo(rs.getString("student_no"));
        return f;
    }
}
