package com.ssm.controller;

import com.ssm.bean.ShopInformationBean;
import com.ssm.bean.UserWantBean;
import com.ssm.pojo.*;
import com.ssm.response.BaseResponse;
import com.ssm.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemController {
    @Resource
    private ShopInformationService shopInformationService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;//获取商品分类的服务
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private UserWantService userWantService;


    @RequestMapping(value = {"/getItemOfthisKind.do"}, method = RequestMethod.POST)
    @ResponseBody
    public  List<ShopInformationBean>  getItem(@RequestParam String sort) {
        int intsort=Integer.parseInt(sort);
        List<ShopInformationBean> list = new ArrayList<>();
        try{
            List<ShopInformation> shopInformations = shopInformationService.selectBySort(intsort);

            String stringBuffer;
            for (ShopInformation shopInformation : shopInformations) {
                stringBuffer = getSortName(shopInformation.getSort());
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setSort(stringBuffer);
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setImage(shopInformation.getImage());
                list.add(shopInformationBean);
            }


            System.out.println("size:"+list.size());
        }catch ( Exception e){
            System.out.println(e.toString());
        }


        //return BaseResponse.success(list.size()+"");
        // return "layui_index";
        return  list;
    }

    @RequestMapping(value = {"/getItemOfWishesBySort.do"}, method = RequestMethod.POST)
    @ResponseBody
    public  List<UserWantBean>  getItemOfWishes(@RequestParam String sort) {
        int intsort=Integer.parseInt(sort);
        List<UserWantBean> list = new ArrayList<>();
        try{
            List<UserWant> shopInformations = userWantService.selectBySort(intsort);

            String stringBuffer;
            for (UserWant shopInformation : shopInformations) {
                stringBuffer = getSortName(shopInformation.getSort());
                UserWantBean shopInformationBean = new UserWantBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformation.setModified(shopInformation.getModified());
              //  shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setSort(stringBuffer);
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setUid(shopInformation.getUid());
                //shopInformationBean.setTransaction(shopInformation.getTransaction());
               // shopInformationBean.setImage(shopInformation.getImage());
                list.add(shopInformationBean);
            }


            System.out.println("size:"+list.size());
        }catch ( Exception e){
            System.out.println(e.toString());
        }


        //return BaseResponse.success(list.size()+"");
        // return "layui_index";
        return  list;
    }
    private String getSortName(int sort) {
        StringBuilder stringBuffer = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        stringBuffer.append(allKinds.getName());
        stringBuffer.append("-");
        stringBuffer.append(classification.getName());
        stringBuffer.append("-");
        stringBuffer.append(specific.getName());
//        System.out.println(sort);
        return stringBuffer.toString();
    }

    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }

}
