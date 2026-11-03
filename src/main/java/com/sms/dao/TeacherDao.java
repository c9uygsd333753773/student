package com.sms.dao;

import com.sms.entity.Teacher;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教师档案 DAO
 */
public class TeacherDao {

    public List<Teacher> findAll() {
        String sql = "SELECT * FROM `teacher` ORDER BY `id`";
        List<Teacher> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Teacher findById(Long id) {
        String sql = "SELECT * FROM `teacher` WHERE `id`=?";
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

    public Teacher findByUserId(Long userId) {
        String sql = "SELECT * FROM `teacher` WHERE `user_id`=?";
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

    public Long insert(Teacher t) {
        String sql = "INSERT INTO `teacher`(`user_id`,`teacher_no`,`name`,`gender`,`phone`,`title`) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, t.getUserId());
            ps.setString(2, t.getTeacherNo());
            ps.setString(3, t.getName());
            ps.setString(4, t.getGender());
            ps.setString(5, t.getPhone());
            ps.setString(6, t.getTitle());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int update(Teacher t) {
        String sql = "UPDATE `teacher` SET `teacher_no`=?,`name`=?,`gender`=?,`phone`=?,`title`=? WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getTeacherNo());
            ps.setString(2, t.getName());
            ps.setString(3, t.getGender());
            ps.setString(4, t.getPhone());
            ps.setString(5, t.getTitle());
            ps.setLong(6, t.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM `teacher` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM `teacher` WHERE `user_id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Teacher map(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getLong("user_id"));
        t.setTeacherNo(rs.getString("teacher_no"));
        t.setName(rs.getString("name"));
        t.setGender(rs.getString("gender"));
        t.setPhone(rs.getString("phone"));
        t.setTitle(rs.getString("title"));
        t.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        return t;
    }
}
