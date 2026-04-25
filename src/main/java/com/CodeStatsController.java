package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entity.OpenCodeAgentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CodeStatsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/codeStats")
    public void codeStats(@RequestBody Map<String, Object> params) {
        String sql = "INSERT INTO push_records (local_ref, local_sha, remote_ref, remote_sha, push_time, changed_files, old_snapshot, new_snapshot) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Object changedFilesObj = params.get("changed_files");
        String changedFiles = "";
        if (changedFilesObj != null) {
            if (changedFilesObj instanceof List) {
                List<?> list = (List<?>) changedFilesObj;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(list.get(i));
                }
                changedFiles = sb.toString();
            } else if (changedFilesObj instanceof String) {
                changedFiles = (String) changedFilesObj;
            } else {
                changedFiles = String.valueOf(changedFilesObj);
            }
        }
        
        jdbcTemplate.update(sql,
            params.get("local_ref"),
            params.get("local_sha"),
            params.get("remote_ref"),
            params.get("remote_sha"),
            params.get("push_time"),
            changedFiles,
            params.get("old_snapshot"),
            params.get("new_snapshot")
        );
    }

    @GetMapping("/api/records")
    public List<Map<String, Object>> getRecords() {
        return jdbcTemplate.queryForList("SELECT * FROM push_records ORDER BY id DESC");
    }

    @GetMapping("/api/records/{id}")
    public Map<String, Object> getRecord(@PathVariable Long id) {
        return jdbcTemplate.queryForMap("SELECT * FROM push_records WHERE id = ?", id);
    }

    @PostMapping("/opencode/request")
    public void reportOpenCodeAgent(@RequestBody OpenCodeAgentRequest body) {
        jdbcTemplate.update(
            "INSERT INTO opencode_request_stats (git_username, session_id, model_id, agent, opencode_version) VALUES (?, ?, ?, ?, ?)",
            body.getGitUsername(),
            body.getSessionID(),
            body.getModelID(),
            body.getAgent(),
            body.getOpencodeVersion());
    }

    /**
     * 在相同过滤条件下返回：总调用次数、按 git_username 汇总、按 model_id 汇总。
     * 筛选条件均为可选，时间与 create_time 比较。
     */
    @GetMapping("/api/opencode-request-stats/aggregate")
    public Map<String, Object> aggregateOpenCodeRequestStats(
            @RequestParam(required = false) String gitUsername,
            @RequestParam(required = false) String modelId,
            @RequestParam(required = false) String agent,
            @RequestParam(required = false) String opencodeVersion,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<Object> args = new ArrayList<>();
        String filter = buildOpenCodeStatsFilter(gitUsername, modelId, agent, opencodeVersion, startTime, endTime, args);
        Object[] argArray = args.toArray();

        String fromWhere = "FROM opencode_request_stats WHERE 1=1 " + filter;
        Long totalCount = jdbcTemplate.queryForObject("SELECT COUNT(*) " + fromWhere, Long.class, argArray);

        List<Map<String, Object>> byGitUsername = jdbcTemplate.queryForList(
                "SELECT git_username, COUNT(*) AS call_count " + fromWhere
                        + " GROUP BY git_username ORDER BY COUNT(*) DESC",
                argArray);

        List<Map<String, Object>> byModelId = jdbcTemplate.queryForList(
                "SELECT model_id, COUNT(*) AS call_count " + fromWhere
                        + " GROUP BY model_id ORDER BY COUNT(*) DESC",
                argArray);

        Map<String, Object> body = new HashMap<>();
        body.put("totalCount", totalCount != null ? totalCount : 0L);
        body.put("byGitUsername", byGitUsername);
        body.put("byModelId", byModelId);
        return body;
    }

    /** 返回形如 "AND ... " 的片段，并向 args 追加对应参数 */
    private static String buildOpenCodeStatsFilter(
            String gitUsername,
            String modelId,
            String agent,
            String opencodeVersion,
            String startTime,
            String endTime,
            List<Object> args) {
        StringBuilder sql = new StringBuilder();
        if (notBlank(gitUsername)) {
            sql.append("AND git_username = ? ");
            args.add(gitUsername.trim());
        }
        if (notBlank(modelId)) {
            sql.append("AND model_id = ? ");
            args.add(modelId.trim());
        }
        if (notBlank(agent)) {
            sql.append("AND agent = ? ");
            args.add(agent.trim());
        }
        if (notBlank(opencodeVersion)) {
            sql.append("AND opencode_version = ? ");
            args.add(opencodeVersion.trim());
        }
        if (notBlank(startTime)) {
            sql.append("AND create_time >= ? ");
            args.add(normalizeDateTimeParam(startTime.trim()));
        }
        if (notBlank(endTime)) {
            sql.append("AND create_time <= ? ");
            args.add(normalizeDateTimeParam(endTime.trim()));
        }
        return sql.toString();
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** 将 datetime-local 常见的 "T" 分隔转为 JDBC/MySQL 易解析的形式 */
    private static String normalizeDateTimeParam(String s) {
        return s.replace('T', ' ');
    }

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/api/v1/push/report")
    public void receivePushReport(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== 接收到推送报告 ===");
            System.out.println("时间戳: " + payload.get("timestamp"));
            System.out.println("钩子类型: " + payload.get("hookType"));
            System.out.println("环境信息: " + objectMapper.writeValueAsString(payload.get("environment")));
            System.out.println("推送摘要: " + objectMapper.writeValueAsString(payload.get("pushSummary")));
            System.out.println("提交信息: " + objectMapper.writeValueAsString(payload.get("commitInfos")));
            System.out.println("OC 部分: " + objectMapper.writeValueAsString(payload.get("ocParts")));
            System.out.println("=== 推送报告接收完成 测试修改===");
            System.out.println("=== 推送报告接收完成 测试新增===");
            System.out.println("=== 推送报告接收完成 测试修改2===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
