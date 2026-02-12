package com.sunny.framework.job.client;

import com.xxl.job.core.biz.model.ReturnT;
import com.sunny.framework.job.model.XxlJobInfo;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "XxlJobAdminClient", url = "${xxl.job.admin.addresses:}")
public interface XxlJobAdminClient {

    @PostMapping(path = "/auth/doLogin")
    Response login(@RequestParam("userName") String userName, @RequestParam("password") String password);

    @RequestMapping(path = "/jobinfo/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT<String> jobInfoAdd(@RequestHeader("Cookie") String cookie, @SpringQueryMap XxlJobInfo jobInfo);

    @RequestMapping(path = "/jobinfo/update", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ReturnT<Void> jobInfoUpdate(@RequestHeader("Cookie") String cookie, @SpringQueryMap XxlJobInfo jobInfo);

    @RequestMapping("/jobinfo/remove")
    ReturnT<Void> jobInfoRemove(@RequestHeader("Cookie") String cookie, @RequestParam("id") int id);

    @RequestMapping("/jobinfo/start")
    ReturnT<Void> jobInfoStart(@RequestHeader("Cookie") String cookie, @RequestParam("id") int id);

    @RequestMapping("/jobinfo/stop")
    ReturnT<Void> jobInfoStop(@RequestHeader("Cookie") String cookie, @RequestParam("id") int id);

    @RequestMapping("/jobinfo/nextTriggerTime")
    ReturnT<List<String>> nextTriggerTime(@RequestHeader("Cookie") String cookie,
                                          @RequestParam("scheduleType") String scheduleType,
                                          @RequestParam("scheduleConf") String scheduleConf);
}
