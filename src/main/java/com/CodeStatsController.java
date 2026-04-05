package com;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class CodeStatsController {

    @PostMapping("/codeStats")
    public void codeStats(@RequestBody Map<String, Object> params) throws JsonProcessingException {
        // 把params转换为json字符串
        String jsonString = new ObjectMapper().writeValueAsString(params);
        System.out.println(jsonString);
    }
}
