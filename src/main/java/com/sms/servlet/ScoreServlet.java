package com.sms.servlet;

import com.sms.dao.ScoreDao;
import com.sms.dao.StudentDao;
import com.sms.entity.Score;
import com.sms.entity.Student;
import com.sms.service.ScoreService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 成绩：教师录入/修改/导出，学生查询、GPA、排名
 */
@WebServlet("/score")
public class ScoreServlet extends BaseServlet {

    private final ScoreService scoreService = new ScoreService();
    private final StudentDao studentDao = new StudentDao();
    private final ScoreDao scoreDao = new ScoreDao();

    /** 学生：我的成绩 */
    public void listMy(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        writeJson(resp, Result.ok(scoreService.listByStudent(getProfileId(req))));
    }

    /** 教师：某课程某班级的成绩录入列表 */
    public void listByCourse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER", "ADMIN");
        Long courseId = longParam(req, "courseId");
        Long classId = longParam(req, "classId");
        if (courseId == null || classId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        writeJson(resp, Result.ok(scoreService.listByCourseAndClass(courseId, classId)));
    }

    /** 教师：保存（录入/修改）单个学生成绩 */
    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER", "ADMIN");
        Score s = new Score();
        s.setStudentId(longParam(req, "studentId"));
        s.setCourseId(longParam(req, "courseId"));
        s.setScore(doubleParam(req, "score"));
        if (s.getStudentId() == null || s.getCourseId() == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        String err = scoreService.save(s);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("成绩已保存"));
    }

    /** 教师/管理员：删除成绩 */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER", "ADMIN");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        scoreService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }

    /** 学生：GPA 与加权平均分 */
    public void myGpa(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        writeJson(resp, Result.ok(scoreService.computeGpa(getProfileId(req))));
    }

    /** 学生：班级排名 */
    public void myRank(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        Student s = studentDao.findById(getProfileId(req));
        if (s == null || s.getClassId() == null) {
            writeJson(resp, Result.fail("未分配班级"));
            return;
        }
        writeJson(resp, Result.ok(scoreService.rankInClass(s.getClassId(), s.getId())));
    }

    /** 教师/管理员：导出某课程某班级成绩 CSV */
    public void export(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER", "ADMIN");
        Long courseId = longParam(req, "courseId");
        Long classId = longParam(req, "classId");
        if (courseId == null || classId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        List<Score> scores = scoreService.listByCourseAndClass(courseId, classId);
        String csv = scoreService.exportCsv(scores);
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=score_course" + courseId + "_class" + classId + ".csv");
        resp.getWriter().write(csv);
    }

    /** 统计：成绩分布 / 班级平均分 / 课程平均分 */
    public void distribution(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        String type = param(req, "type");
        Long id = longParam(req, "id");
        List<Double> values;
        if ("class".equals(type) && id != null) {
            values = scoreDao.findScoreValuesByClass(id);
        } else if ("course".equals(type) && id != null) {
            values = scoreDao.findScoreValuesByCourse(id);
        } else {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        writeJson(resp, Result.ok(scoreService.distribution(values)));
    }

    public void classAverage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        writeJson(resp, Result.ok(scoreService.classAverages()));
    }

    public void courseAverage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        writeJson(resp, Result.ok(scoreService.courseAverages()));
    }
}
