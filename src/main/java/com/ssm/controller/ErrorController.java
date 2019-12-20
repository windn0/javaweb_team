package com.ssm.controller;

import com.ssm.dao.ShopCarMapper;
import com.ssm.pojo.ShopCar;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;


@Controller
public class ErrorController {

    @Resource
    private ShopCarMapper shopCarMapper;

    @RequestMapping(value = {"/error.do"})

    public String error() {
       // ShopCar shopCar =   shopCarMapper.selectByUid(7);
       // int id=shopCar.getId();
       return "template/error";
       // return "layui_index";

    }

}
