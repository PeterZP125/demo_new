package com.peter.mall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.UmsMember;
import com.peter.mall.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UmsMemberController {

    @Reference
    UmsMemberService umsMemberService;

    @RequestMapping("/getUmsMemberById")
    public List<UmsMember> getUmsMemberById(@RequestParam("id") String id) {
        return umsMemberService.getUmsMemberById(id);
    }
}
