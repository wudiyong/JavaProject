package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/api/records")
    public List<Map<String, Object>> getRecords() {
        return jdbcTemplate.queryForList("SELECT * FROM push_records ORDER BY id DESC");
    }

    @GetMapping("/api/records/{id}")
    public Map<String, Object> getRecord(@PathVariable Long id) {
        return jdbcTemplate.queryForMap("SELECT * FROM push_records WHERE id = ?", id);
    }
}
