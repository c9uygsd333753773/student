package com.sms.service;

import com.sms.dao.ScoreDao;
import com.sms.entity.Score;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩业务：录入、查询、GPA、排名、统计、导出
 */
public class ScoreService {

    private final ScoreDao scoreDao = new ScoreDao();

    public List<Score> listByStudent(Long studentId) {
        return scoreDao.findByStudent(studentId);
    }

    public List<Score> listByCourseAndClass(Long courseId, Long classId) {
        return scoreDao.findByCourseAndClass(courseId, classId);
    }

    public String save(Score s) {
        if (s.getScore() == null) return "成绩不能为空";
        if (s.getScore() < 0 || s.getScore() > 100) return "成绩必须在 0-100 之间";
        scoreDao.upsert(s);
        return null;
    }

    public void delete(Long id) {
        scoreDao.delete(id);
    }

    /** 绩点换算（4.0 制） */
    public static double gpaPoint(double score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0.0;
    }

    /** 计算学生加权平均分与 GPA */
    public Map<String, Object> computeGpa(Long studentId) {
        List<Score> scores = scoreDao.findByStudent(studentId);
        double totalScoreCredit = 0, totalCredit = 0, totalGpaCredit = 0;
        for (Score s : scores) {
            if (s.getScore() == null || s.getCredit() == null) continue;
            double credit = s.getCredit();
            totalScoreCredit += s.getScore() * credit;
            totalGpaCredit += gpaPoint(s.getScore()) * credit;
            totalCredit += credit;
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("weightedAvg", totalCredit == 0 ? null : Math.round(totalScoreCredit / totalCredit * 100.0) / 100.0);
        m.put("gpa", totalCredit == 0 ? null : Math.round(totalGpaCredit / totalCredit * 100.0) / 100.0);
        m.put("totalCredit", totalCredit);
        m.put("courseCount", scores.size());
        return m;
    }

    /** 学生在班级内的排名 */
    public Map<String, Object> rankInClass(Long classId, Long studentId) {
        List<Map<String, Object>> ranking = scoreDao.findClassRanking(classId);
        int rank = 0;
        int total = ranking.size();
        for (int i = 0; i < ranking.size(); i++) {
            if (Long.valueOf(String.valueOf(ranking.get(i).get("studentId"))).equals(studentId)) {
                rank = i + 1;
                break;
            }
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("rank", rank);
        m.put("total", total);
        m.put("ranking", ranking);
        return m;
    }

    /** 成绩分布（分数段人数统计） */
    public Map<String, Object> distribution(List<Double> values) {
        int[] buckets = new int[5]; // 0-59,60-69,70-79,80-89,90-100
        for (Double v : values) {
            if (v == null) continue;
            if (v < 60) buckets[0]++;
            else if (v < 70) buckets[1]++;
            else if (v < 80) buckets[2]++;
            else if (v < 90) buckets[3]++;
            else buckets[4]++;
        }
        String[] labels = {"0-59", "60-69", "70-79", "80-89", "90-100"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("range", labels[i]);
            m.put("count", buckets[i]);
            list.add(m);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("distribution", list);
        result.put("total", values.size());
        return result;
    }

    public Map<String, Object> classAverages() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("list", scoreDao.findClassAverages());
        return m;
    }

    public Map<String, Object> courseAverages() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("list", scoreDao.findCourseAverages());
        return m;
    }

    /** 导出 CSV（含 BOM，兼容 Excel 中文） */
    public String exportCsv(List<Score> scores) {
        StringBuilder sb = new StringBuilder("﻿");
        sb.append("学号,姓名,课程ID,成绩\n");
        for (Score s : scores) {
            sb.append(s.getStudentNo()).append(",")
              .append(s.getStudentName() == null ? "" : s.getStudentName()).append(",")
              .append(s.getCourseId()).append(",")
              .append(s.getScore() == null ? "" : s.getScore()).append("\n");
        }
        return sb.toString();
    }
}
