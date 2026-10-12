package com.sunny.framework.job.client;

import com.xxl.job.core.openapi.admin.AdminJobBiz;
import com.xxl.job.core.openapi.admin.dto.JobAddRequest;
import com.xxl.job.core.openapi.admin.dto.JobOperateRequest;
import com.xxl.job.core.openapi.admin.dto.JobTriggerRequest;
import com.xxl.job.core.openapi.admin.dto.JobUpdateRequest;
import com.xxl.tool.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "XxlJobApiClient", url = "${xxl.job.admin.addresses:}", path = "api", configuration = XxlJobApiConfig.class)
public interface XxlJobApiClient extends AdminJobBiz {
    @RequestMapping(value = "addJob", method = RequestMethod.POST)
    Response<String> addJob(@RequestBody JobAddRequest request);

    @RequestMapping(value = "updateJob", method = RequestMethod.POST)
    Response<String> updateJob(@RequestBody JobUpdateRequest request);

    @RequestMapping(value = "removeJob", method = RequestMethod.POST)
    Response<String> removeJob(@RequestBody JobOperateRequest request);

    @RequestMapping(value = "startJob", method = RequestMethod.POST)
    Response<String> startJob(@RequestBody JobOperateRequest request);

    @RequestMapping(value = "stopJob", method = RequestMethod.POST)
    Response<String> stopJob(@RequestBody JobOperateRequest request);

    @RequestMapping(value = "triggerJob", method = RequestMethod.POST)
    Response<String> triggerJob(@RequestBody JobTriggerRequest request);

    @RequestMapping(value = "nextTriggerTime", method = RequestMethod.POST)
    Response<List<String>> nextTriggerTime(@RequestBody JobTriggerRequest request);
}
