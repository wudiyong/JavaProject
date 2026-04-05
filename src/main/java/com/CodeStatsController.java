package com;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class CodeStatsController {

    @PostMapping("/codeStats")
    public void codeStats(@RequestBody Map<String, Object> params) {
        System.out.println(params);
        System.out.println("codeStats");
    }
}
