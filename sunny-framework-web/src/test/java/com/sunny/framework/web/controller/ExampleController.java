package com.sunny.framework.web.controller;

import com.sunny.framework.core.model.CommonResult;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
public class ExampleController {

    @Data
    public static class Form {
        private String name;
        private Date date;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
        private List<LocalDateTime> range;
    }

    @PostMapping(path = "/form")
    public CommonResult<Form> form(@RequestBody Form form) {
        return CommonResult.ok(form);
    }

}
