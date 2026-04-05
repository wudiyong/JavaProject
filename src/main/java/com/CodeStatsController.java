package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class CodeStatsController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/codeStats")
    public void codeStats(@RequestBody Map<String, Object> params) {
        String sql = "INSERT INTO push_records (local_ref, local_sha, remote_ref, remote_sha, push_time, changed_files, diff) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
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
            params.get("diff")
        );
    }

    @GetMapping("/")
    public String index() {
        return "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<title>Push Records</title>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; margin: 20px; }" +
            "table { border-collapse: collapse; width: 100%; }" +
            "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
            "th { background-color: #4CAF50; color: white; }" +
            "tr:nth-child(even) { background-color: #f2f2f2; }" +
            ".diff-content { max-height: 200px; overflow-y: auto; white-space: pre-wrap; font-family: monospace; font-size: 12px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<h1>Git Push Records</h1>" +
            "<div id='records'></div>" +
            "<script>" +
            "fetch('/api/records').then(r => r.json()).then(data => {" +
            "  let html = '<table><tr><th>ID</th><th>Local Ref</th><th>Local SHA</th><th>Remote Ref</th><th>Remote SHA</th><th>Push Time</th><th>Changed Files</th><th>Diff</th></tr>';" +
            "  data.forEach(row => {" +
            "    html += '<tr>';" +
            "    html += '<td>' + row[0] + '</td>';" +
            "    html += '<td>' + row[1] + '</td>';" +
            "    html += '<td>' + (row[2] ? row[2].substring(0, 7) : '') + '</td>';" +
            "    html += '<td>' + row[3] + '</td>';" +
            "    html += '<td>' + (row[4] ? row[4].substring(0, 7) : '') + '</td>';" +
            "    html += '<td>' + row[5] + '</td>';" +
            "    html += '<td>' + row[6] + '</td>';" +
            "    html += '<td><div class=\"diff-content\">' + (row[7] || '') + '</div></td>';" +
            "    html += '</tr>';" +
            "  });" +
            "  html += '</table>';" +
            "  document.getElementById('records').innerHTML = html;" +
            "});" +
            "</script>" +
            "</body>" +
            "</html>";
    }

    @GetMapping("/api/records")
    public List<Map<String, Object>> getRecords() {
        return jdbcTemplate.queryForList("SELECT * FROM push_records ORDER BY id DESC");
    }
}
