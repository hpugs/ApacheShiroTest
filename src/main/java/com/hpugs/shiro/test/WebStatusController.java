package com.hpugs.shiro.test;

import com.hpugs.base.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xinge
 * @date 2022-08-04
 * 服务健康检查controller
 */
@Slf4j
@RestController
public class WebStatusController {

    @RequestMapping(value = "/webStatus", method = {RequestMethod.GET, RequestMethod.HEAD})
    @ResponseBody
    public Result welcome(HttpServletRequest servletRequest) {
        log.info("服务状态：SUCCESS");
        return Result.buildSuccess();
    }

}
