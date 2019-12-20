package com.ssm.controller;

import com.ssm.bean.GoodsCarBean;
import com.ssm.bean.ShopInformationBean;
import com.ssm.bean.UserWantBean;
import com.ssm.dao.ShopCarMapper;
import com.ssm.dao.UserInformationMapper;
import com.ssm.pojo.*;
import com.ssm.response.BaseResponse;
import com.ssm.service.*;
import com.ssm.token.TokenProccessor;
import com.ssm.tool.StringUtils;
//import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class  indexController {

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
    @Resource
    private GoodsCarService goodsCarService;




    @RequestMapping(value = {"/","/main.do"})
    public String getIndex(HttpServletRequest request, HttpSession session, Model model) {
//      UserInformation userInformation=userInformationMapper.selectByPrimaryKey(1);
//        String s=userInformation.getUsername();
//        System.out.println(s);
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        // if user login,the session will have the "userInformation"
        if (!StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            model.addAttribute("userInformation", userInformation);
            session.setAttribute("userInformation2", userInformation);//似乎占用了另一个名字，所以2吧
        } else {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
            session.setAttribute("userInformation2", userInformation);
        }
        //一般形式进入首页
        try {
            List<ShopInformation> shopInformations = selectTen(1, 5);
            List<ShopInformationBean> list = new ArrayList<>();
            int counts = getShopCounts();
            model.addAttribute("shopInformationCounts", counts);
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
            model.addAttribute("shopInformationBean", list);

            System.out.println(list.size());
        } catch (Exception e) {
            e.printStackTrace();
            return "layui_login.do";
        }
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
    @RequestMapping(value = {"/shop_book.do"})
    public String getBookShop() {
        return "shop_book";
    }
    @RequestMapping(value = {"/shop_clothes.do"})
    public String getClothesShop() {
        return "shop_clothes";
    }
    @RequestMapping(value = {"/shop_sport.do"})
    public String getSportShop() {
        return "shop_sport";
    }


    @RequestMapping(value = {"/shop_householdwish.do"})
    public String getHouseHoldShopWish() {
        return "shop_householdwish";
    }

    @RequestMapping(value = {"/shop_digitalwish.do"})
    public String getDigitalShopWish() {
        return "shop_digitalwish";
    }
    @RequestMapping(value = {"/shop_bookwish.do"})
    public String getBookShopWish() {
        return "shop_bookwish";
    }
    @RequestMapping(value = {"/shop_clotheswish.do"})
    public String getClothesShopWish() {
        return "shop_clotheswish";
    }
    @RequestMapping(value = {"/shop_sportwish.do"})
    public String getSportShopWish() {
        return "shop_sportwish";
    }



    @RequestMapping(value = {"/layui_cart.do"})
    public String selectShopCar(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            userInformation = new UserInformation();
            model.addAttribute("userInformation", userInformation);
//            list.add(shopCar);
            return "redirect:/layui_login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = userInformation.getId();
        List<GoodsCar> goodsCars = goodsCarService.selectByUid(uid);
        List<GoodsCarBean> goodsCarBeans = new ArrayList<>();
        for (GoodsCar goodsCar : goodsCars) {
            GoodsCarBean goodsCarBean = new GoodsCarBean();
            goodsCarBean.setUid(goodsCar.getUid());
            goodsCarBean.setSid(goodsCar.getSid());
            goodsCarBean.setModified(goodsCar.getModified());
            goodsCarBean.setId(goodsCar.getId());
            goodsCarBean.setQuantity(goodsCar.getQuantity());
            ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(goodsCar.getSid());
            goodsCarBean.setName(shopInformation.getName());
            goodsCarBean.setRemark(shopInformation.getRemark());
            goodsCarBean.setImage(shopInformation.getImage());
            goodsCarBean.setPrice(shopInformation.getPrice().doubleValue());
            goodsCarBean.setSort(getSort(shopInformation.getSort()));
            goodsCarBeans.add(goodsCarBean);
        }
        model.addAttribute("list", goodsCarBeans);
        return "layui_cart";
    }


    @RequestMapping(value = {"/layui_login.do"})
    public String getLoginPage(HttpServletRequest request, Model model) {
        String token = TokenProccessor.getInstance().makeToken();
        //log.info("进入登录界面，token为:" + token);
        request.getSession().setAttribute("token", token);
        model.addAttribute("token", token);
        // Log.debug("hello2");
        System.out.println("have you ");

        return "layui_login";
    }

    @RequestMapping(value = {"/layui_mypublish.do"})
    public String getMyPublish(HttpServletRequest request, Model model) {

        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/layui_login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
        int uid = (int) request.getSession().getAttribute("uid");
        List<ShopInformation> shopInformations = shopInformationService.selectUserReleaseByUid(uid);
        List<ShopInformationBean> list = new ArrayList<>();
        String stringBuffer;
//            int i=0;
        for (ShopInformation shopInformation : shopInformations) {
//                if (i>=5){
//                    break;
//                }
//                i++;
            stringBuffer = getSort(shopInformation.getSort());
            ShopInformationBean shopInformationBean = new ShopInformationBean();
            shopInformationBean.setId(shopInformation.getId());
            shopInformationBean.setName(shopInformation.getName());
            shopInformationBean.setLevel(shopInformation.getLevel());
            shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
            shopInformationBean.setRemark(shopInformation.getRemark());
            shopInformationBean.setSort(stringBuffer);
            shopInformationBean.setQuantity(shopInformation.getQuantity());
            shopInformationBean.setTransaction(shopInformation.getTransaction());
            shopInformationBean.setUid(shopInformation.getUid());
            shopInformationBean.setImage(shopInformation.getImage());
            list.add(shopInformationBean);
        }
        model.addAttribute("shopInformationBean", list);
        return "layui_mypublish";

    }

    @RequestMapping(value = {"/layui_mywishes.do"})
    public String getMyWishes(HttpServletRequest request, Model model)
    {
        List<UserWant> list;
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/layui_login.do";
        }
        try {
            int uid = (int) request.getSession().getAttribute("uid");
//            list = selectUserWantByUid(4);
            list = selectUserWantByUid(uid);
            List<UserWantBean> userWantBeans = new ArrayList<>();
            for (UserWant userWant : list) {
                UserWantBean userWantBean = new UserWantBean();
                userWantBean.setId(userWant.getId());
                userWantBean.setModified(userWant.getModified());
                userWantBean.setName(userWant.getName());
                userWantBean.setPrice(userWant.getPrice().doubleValue());
                userWantBean.setUid(uid);
                userWantBean.setQuantity(userWant.getQuantity());
                userWantBean.setRemark(userWant.getRemark());
                userWantBean.setSort(getSort(userWant.getSort()));
                userWantBeans.add(userWantBean);
            }
            model.addAttribute("userWantBean", userWantBeans);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        model.addAttribute("userInformation", userInformation);
        return "layui_mywishes";
    }

    @RequestMapping(value = {"/layui_publish.do"})
    public String getPublish(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //如果没有登录
            return "redirect:/layui_login.do";
        } else {
            model.addAttribute("userInformation", userInformation);
        }
       // 如果登录了，判断该用户有没有经过认证
        try {
            String realName = userInformation.getRealname();
            String sno = userInformation.getSno();
            String dormitory = userInformation.getDormitory();
            if (StringUtils.getInstance().isNullOrEmpty(realName) || StringUtils.getInstance().isNullOrEmpty(sno) || StringUtils.getInstance().isNullOrEmpty(dormitory)) {
                //没有
                model.addAttribute("message", "请先认证真实信息");//前端要给一个提示呀！！烦心！
                return "redirect:layui_selfInfo.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/layui_login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("shopInformation", new ShopInformation());
        model.addAttribute("action", 1);
        model.addAttribute("token", goodsToken);
       // return "page/publish_product";


        return "layui_publish";
    }

    @RequestMapping(value = {"/layui_wishes.do"})
    public String enterPublishUserWant(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/layui_login.do";
        }
        String error = request.getParameter("error");
        if (!StringUtils.getInstance().isNullOrEmpty(error)) {
            model.addAttribute("error", "error");
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("action",1);//正常进入发布求购的标志，为2 则为修改。
        model.addAttribute("id",-1);//正常进入用作-1

        model.addAttribute("userInformation", userInformation);
        return "layui_wishes";
    }
    @RequestMapping(value = {"/layui_register.do"})
    public String getRegister(HttpServletRequest request, Model model) {

        String Token = (String) request.getSession().getAttribute("token");
       // model.addAttribute("token", token);
        // Log.debug("hello2");
        if(!StringUtils.getInstance().isNullOrEmpty(Token) ){
            return "layui_register";
        }


        //System.out.println("have you ");
        return "error.do";

    }
    @RequestMapping(value = {"/layui_selfInfo.do"})
    public String getSelfInfo(HttpServletRequest request, Model model) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/layui_login.do";
        }
        String personalInfoToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("personalInfoToken", personalInfoToken);
        model.addAttribute("token", personalInfoToken);
        model.addAttribute("userInformation", userInformation);

        return "layui_selfInfo";
    }

    @RequestMapping(value = {"/layui_search.do"})

    public String getSearch( HttpServletRequest request, Model model,@RequestParam(required = false) String name) {

        System.out.println("进入到搜索");
       try {
            System.out.println(name);
            List<ShopInformation> shopInformations = shopInformationService.selectByName(name);
            UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
            if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
                userInformation = new UserInformation();
                model.addAttribute("userInformation", userInformation);
            } else {
                model.addAttribute("userInformation", userInformation);
            }
            List<ShopInformationBean> shopInformationBeans = new ArrayList<>();
            String sortName;
            for (ShopInformation shopInformation : shopInformations) {
                int sort = shopInformation.getSort();
                sortName = getSort(sort);
                ShopInformationBean shopInformationBean = new ShopInformationBean();
                shopInformationBean.setId(shopInformation.getId());
                shopInformationBean.setName(shopInformation.getName());
                shopInformationBean.setLevel(shopInformation.getLevel());
                shopInformationBean.setRemark(shopInformation.getRemark());
                shopInformationBean.setPrice(shopInformation.getPrice().doubleValue());
                shopInformationBean.setQuantity(shopInformation.getQuantity());
                shopInformationBean.setTransaction(shopInformation.getTransaction());
                shopInformationBean.setSort(sortName);
                shopInformationBean.setUid(shopInformation.getUid());
                shopInformationBean.setImage(shopInformation.getImage());
                shopInformationBeans.add(shopInformationBean);
            }
            model.addAttribute("shopInformationBean", shopInformationBeans);
        } catch (Exception e ) {
            e.printStackTrace();
           return "redirect:layui_index.do";
        }
        //return "page/mall_page";
        return "layui_search";
    }

    //获取商品，分页,一次性获取end个
    private List<ShopInformation> selectTen(int start, int end) {
        Map map = new HashMap();
        map.put("start", (start - 1) * end);
        map.put("end", end);
        List<ShopInformation> list = shopInformationService.selectTen(map);
        return list;
    }
    private int getShopCounts() {
        return shopInformationService.getCounts();
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

    private List<UserWant> selectUserWantByUid(int uid) {
        try {
            return userWantService.selectMineByUid(uid);
        } catch (Exception e) {
            e.printStackTrace();
            List<UserWant> list = new ArrayList<>();
            list.add(new UserWant());
            return list;
        }
    }



    private String getSort(int sort) {
        StringBuilder sb = new StringBuilder();
        Specific specific = selectSpecificBySort(sort);
        int cid = specific.getCid();
        Classification classification = selectClassificationByCid(cid);
        int aid = classification.getAid();
        AllKinds allKinds = selectAllKindsByAid(aid);
        sb.append(allKinds.getName());
        sb.append("-");
        sb.append(classification.getName());
        sb.append("-");
        sb.append(specific.getName());
        return sb.toString();
    }

}

