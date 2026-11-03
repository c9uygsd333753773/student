package com.sms.dao;

import com.sms.entity.Notice;
import com.sms.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告通知 DAO
 */
public class NoticeDao {

    /** 全部公告 */
    public List<Notice> findAll() {
        String sql = "SELECT n.*, cl.name AS class_name FROM `notice` n " +
                "LEFT JOIN `clazz` cl ON n.class_id = cl.id ORDER BY n.created_at DESC";
        List<Notice> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /** 学生可见的公告：全站公告 + 本班公告 */
    public List<Notice> findVisibleForStudent(Long classId) {
        String sql = "SELECT n.*, cl.name AS class_name FROM `notice` n " +
                "LEFT JOIN `clazz` cl ON n.class_id = cl.id " +
                "WHERE n.scope='ALL' OR (n.scope='CLASS' AND n.class_id=?) " +
                "ORDER BY n.created_at DESC";
        List<Notice> list = new ArrayList<>();
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

    /** 某发布者（教师/管理员）发布的公告 */
    public List<Notice> findByPublisher(Long publisherId, String role) {
        String sql = "SELECT n.*, cl.name AS class_name FROM `notice` n " +
                "LEFT JOIN `clazz` cl ON n.class_id = cl.id " +
                "WHERE n.publisher_id=? AND n.publisher_role=? ORDER BY n.created_at DESC";
        List<Notice> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, publisherId);
            ps.setString(2, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Long insert(Notice n) {
        String sql = "INSERT INTO `notice`(`title`,`content`,`publisher_id`,`publisher_role`,`scope`,`class_id`) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getTitle());
            ps.setString(2, n.getContent());
            ps.setLong(3, n.getPublisherId());
            ps.setString(4, n.getPublisherRole());
            ps.setString(5, n.getScope());
            if (n.getClassId() == null) ps.setNull(6, Types.BIGINT);
            else ps.setLong(6, n.getClassId());
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
        String sql = "DELETE FROM `notice` WHERE `id`=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Notice map(ResultSet rs) throws SQLException {
        Notice n = new Notice();
        n.setId(rs.getLong("id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setPublisherId(rs.getLong("publisher_id"));
        n.setPublisherRole(rs.getString("publisher_role"));
        n.setScope(rs.getString("scope"));
        long cl = rs.getLong("class_id");
        n.setClassId(rs.wasNull() ? null : cl);
        n.setCreatedAt(DBUtil.fmt(rs.getTimestamp("created_at")));
        n.setClassName(rs.getString("class_name"));
        return n;
    }
}
