package com.ssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by wsk1103 on 2017/5/23.
 */
@Controller
public class  indexController {

    @RequestMapping(value = {"/","/main.do"})
    public String getIndex() {
        return "layui_index";
    }

    @RequestMapping(value = {"/shop_household.do"})
    public String getHouseHoldShop() {
        return "shop_household";
    }

    @RequestMapping(value = {"/shop_digital.do"})
    public String getDigitalShop() {
        return "shop_digital";
    }



}

