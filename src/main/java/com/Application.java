package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 执行schema.sql文件来初始化数据库表结构
        try (InputStream inputStream = getClass().getResourceAsStream("/schema.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line);
                if (line.endsWith(";")) {
                    jdbcTemplate.execute(sql.toString());
                    sql.setLength(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
