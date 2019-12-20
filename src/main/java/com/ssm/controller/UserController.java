package com.ssm.controller;

import com.alibaba.fastjson.JSONArray;
import com.ssm.bean.ShopInformationBean;
import com.ssm.pojo.*;
import com.ssm.response.BaseResponse;
import com.ssm.service.*;
import com.ssm.token.TokenProccessor;
import com.ssm.tool.SaveSession;
import com.ssm.tool.StringUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;


@Controller
public class UserController {
    @Resource
    private UserInformationService userInformationService;
    @Resource
    private UserPasswordService userPasswordService;
    @Resource
    private ShopInformationService shopInformationService;

    @Resource
    private UserReleaseService userReleaseService;
    @Resource
    private SpecificeService specificeService;
    @Resource
    private ClassificationService classificationService;
    @Resource
    private AllKindsService allKindsService;
    @Resource
    private UserWantService userWantService;
    @Resource
    private GoodsCarService goodsCarService;

    @ResponseBody
    @RequestMapping(value = "/Login2Index.do",method = RequestMethod.POST)
    public BaseResponse login(HttpServletRequest request,
                              @RequestParam String phone, @RequestParam String password, @RequestParam String token) {
        String loginToken = (String) request.getSession().getAttribute("token");
        if (StringUtils.getInstance().isNullOrEmpty(phone) || StringUtils.getInstance().isNullOrEmpty(password)) {

            return BaseResponse.fail("表单为空");
        }
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(token) || !token.equals(loginToken)) {

            return BaseResponse.fail("重复提交");
        }

        boolean b = getId(phone, password, request);
        //失败，不存在该手机号码
        if (!b) {
            return BaseResponse.fail("不存在该手机号码或者密码错误");
        }
        return BaseResponse.success("/");

    }

    @RequestMapping(value = "/layui_logout.do")
    public String logout(HttpServletRequest request) {
        try {
            request.getSession().removeAttribute("userInformation");
            request.getSession().removeAttribute("uid");
            System.out.println("logout");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home.do";
        }
        return "redirect:/layui_login.do";
    }

    @ResponseBody
    @RequestMapping(value = "/Toregistered.do", method = RequestMethod.POST)
    public BaseResponse registered(@RequestParam String name, @RequestParam String phone, @RequestParam String password) {
        UserInformation userInformation = new UserInformation();
        userInformation.setUsername(name);
        userInformation.setPhone(phone);
        userInformation.setModified(new Date());
        userInformation.setCreatetime(new Date());
        if (userInformationService.insertSelective(userInformation) == 1) {
            int uid = userInformationService.selectIdByPhone(phone);
            UserPassword userPassword = new UserPassword();
            userPassword.setModified(new Date());
            password = StringUtils.getInstance().getMD5(password);
            userPassword.setPassword(password);
            userPassword.setUid(uid);
            int result = userPasswordService.insertSelective(userPassword);
            if (result != 1) {
                //model.addAttribute("result", "fail");

                return BaseResponse.fail("注册失败");
            }
           // model.addAttribute("result", "success");
            return BaseResponse.success("注册成功，请返回登录");

        }
      //  model.addAttribute("result", "fail");

        return BaseResponse.fail("注册失败");
    }
    private boolean getId(String phone, String password, HttpServletRequest request) {
        int uid = userInformationService.selectIdByPhone(phone);
        if (uid == 0 || StringUtils.getInstance().isNullOrEmpty(uid)) {
            System.out.println("xx");
            return false;
        }
        UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
        if (null == userInformation) {
            System.out.println("xxx");
            return false;
        }
        password = StringUtils.getInstance().getMD5(password);
        String password2 = userPasswordService.selectByUid(userInformation.getId()).getPassword();
        if (!password.equals(password2)) {
            System.out.println("xxxx");
            return false;
        }
        //如果密码账号对应正确，将userInformation存储到session中
        request.getSession().setAttribute("userInformation", userInformation);
        request.getSession().setAttribute("uid", uid);
        SaveSession.getInstance().save(phone, System.currentTimeMillis());
        System.out.println("密码正确");
        return true;
    }

//    @RequestMapping(value = {"/verifyUploadPic.do"})
//    @ResponseBody
//    public BaseResponse verifyPic() {
//
//        return BaseResponse.success("success");
//    }
    @RequestMapping(value="/verifyUploadPic.do",method=RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody//json交互注解
    public List<String> uploadResource(MultipartFile file) throws Exception{
     //   UserExtend teacher = (UserExtend) session.getAttribute("userInfo");
        //String path = session.getServletContext().getRealPath("\\resource\\"+teacher.getUserCode());//这样只会保存到项目的文件目录下
        List<String> success=new ArrayList<>();
        String getrandom= StringUtils.getInstance().getRandomChar() + System.currentTimeMillis() + ".jpg";
        String random ="image\\" +getrandom;
       // String random ="image/" +getrandom;
        String path = "E:\\ProjectHub\\javaweb\\ssm\\ssm_teamwork\\src\\main\\webapp\\"+random;
       // String path = "/usr/local/tomcat8/webapps/ssm/"+random; //linux路径
        String fileName = file.getOriginalFilename();
       // File dir = new File(path,fileName);
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //MultipartFile自带的解析方法
        file.transferTo(dir);
        //upload要求返回的数据格式
//        Map<String, Object> uploadData = new HashMap<String, Object>();
//        //JSONArray data = JSONArray.fromObject(dir.getPath());
//        uploadData.put("code", "0");
//        uploadData.put("msg", "");
//        //将文件路径返回
//        uploadData.put("data", "{\'src\':\'"+dir.getPath()+"\'}");
        //生成缩略图

        StringBuilder thumbnails = new StringBuilder();
        StringBuilder wsk = new StringBuilder();
        thumbnails.append("E:\\ProjectHub\\javaweb\\ssm\\ssm_teamwork\\src\\main\\webapp\\");
       // thumbnails.append("/usr/local/tomcat8/webapps/ssm/");
        thumbnails.append("image\\thumbnails\\");
        //thumbnails.append("image/thumbnails/");//linux路径
        wsk.append(StringUtils.getInstance().getRandomChar()).append(System.currentTimeMillis()).append(".jpg");
        thumbnails.append(wsk);
//        File thumbnailsFile = new File(thumbnails.toString());
//        if (!thumbnailsFile.exists()) {
//            thumbnailsFile.mkdir();
//        }
        try{
            //String save = "/images/thumbnails/" + wsk.toString();
            String save = "/images/thumbnails/" + wsk.toString();//？咦这个路径啥意思我都忘了
            Thumbnails.of(path).size(215, 229).toFile(thumbnails.toString());
            success.add(save);
            System.out.println(save);

        } catch ( Exception e) {
            System.out.println("error=生成缩略图失败");
            System.out.println(e.toString());
            //return  BaseResponse.fail("error=生成缩略图失败");
        }
        success.add("/image/"+getrandom);
        System.out.println(getrandom);

        return success;
    }
    //从我的里面修改发布消息进入到发布界面
    @RequestMapping(value = "/modifiedMyPublishProduct.do")
    public String modifiedMyPublishProduct(HttpServletRequest request, Model model,
                                           @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/layui_login.do";
        }
        String goodsToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("goodsToken", goodsToken);
        model.addAttribute("token", goodsToken);
        ShopInformation shopInformation = shopInformationService.selectByPrimaryKey(id);
        model.addAttribute("userInformation", userInformation);
        model.addAttribute("shopInformation", shopInformation);
        model.addAttribute("action", 2);
        model.addAttribute("sort", getSort(shopInformation.getSort()));
        return "layui_publish";
    }


    @RequestMapping(value = "/insertGoods.do", method = RequestMethod.POST)
    public String insertGoods(@RequestParam String name, @RequestParam int level,
                              @RequestParam String remark, @RequestParam double price,
                              @RequestParam int sort, @RequestParam int quantity,
                              @RequestParam String token, @RequestParam(required = false) String image,
                              @RequestParam int action, @RequestParam(required = false) int id, @RequestParam(required = false) String save,
                              HttpServletRequest request, Model model) {
        String goodsToken = (String) request.getSession().getAttribute("goodsToken");
//        String publishProductToken = TokenProccessor.getInstance().makeToken();
//        request.getSession().setAttribute("token",publishProductToken);
        //防止重复提交
        if (StringUtils.getInstance().isNullOrEmpty(goodsToken) || !goodsToken.equals(token)) {
            return "redirect:layui_publish.do?error=1";
        } else {
            request.getSession().removeAttribute("goodsToken");
        }
//        //从session中获得用户的基本信息
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        model.addAttribute("userInformation", userInformation);
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //如果用户不存在，
            return "redirect:/layui_publish.do";
        }
        name = StringUtils.getInstance().replaceBlank(name);
        System.out.println(name);
        remark = StringUtils.getInstance().replaceBlank(remark);
        //judge the data`s format
        if (StringUtils.getInstance().isNullOrEmpty(name) || StringUtils.getInstance().isNullOrEmpty(level) || StringUtils.getInstance().isNullOrEmpty(remark) || StringUtils.getInstance().isNullOrEmpty(price)
                || StringUtils.getInstance().isNullOrEmpty(sort) || StringUtils.getInstance().isNullOrEmpty(quantity) || name.length() > 25 || remark.length() > 122) {
            model.addAttribute("message", "请输入正确的格式!!!!!");
            model.addAttribute("token", goodsToken);
            request.getSession().setAttribute("goodsToken", goodsToken);
            return "layui_publish.do";
        }
        //插入
        if (action == 1) {
            if (StringUtils.getInstance().isNullOrEmpty(image)) {
                model.addAttribute("message", "请选择图片!!!");
                model.addAttribute("token", goodsToken);
                request.getSession().setAttribute("goodsToken", goodsToken);
                System.out.println("error=请插入图片");
                return "redirect:layui_publish.do?error=请插入图片";
            }
//            String random;
//            String path = "D:\\", save = "";
//            random = "image\\" + StringUtils.getInstance().getRandomChar() + System.currentTimeMillis() + ".jpg";
//            StringBuilder thumbnails = new StringBuilder();
//            thumbnails.append(path);
//            thumbnails.append("image/thumbnails/");
//            StringBuilder wsk = new StringBuilder();
//            wsk.append(StringUtils.getInstance().getRandomChar()).append(System.currentTimeMillis()).append(".jpg");
//            thumbnails.append(wsk);
////        String fileName = "\\" + random + ".jpg";
//            File file = new File(path, random);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//            try {
//                image.transferTo(file);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
///*            String pornograp = Pornographic.CheckPornograp("D:\\" + random);
//            if (pornograp.equals("色情图片")) {
//                return "redirect:publish_product?error=不能使用色情图片";
//            }
//            if (!OCR.isOk2(pornograp)) {
//                return "redirect:publish_product?error=图片不能含有敏感文字";
//            }*/
//            //创建缩略图文件夹
//            File thumbnailsFile = new File(thumbnails.toString());
//            if (!thumbnailsFile.exists()) {
//                thumbnailsFile.mkdir();
//            }
//            if (StringUtils.getInstance().thumbnails(path + random, thumbnails.toString())) {
//                save = "/images/thumbnails/" + wsk.toString();
//            } else {
//                System.out.println("error=生成缩略图失败");
//                return "redirect:layui_publish.do?error=生成缩略图失败";
//            }
            //begin insert the shopInformation to the MySQL
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setName(name);
            shopInformation.setLevel(level);
            shopInformation.setRemark(remark);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setSort(sort);
            shopInformation.setQuantity(quantity);
            shopInformation.setModified(new Date());
            shopInformation.setImage(image);//This is the other uniquely identifies
            shopInformation.setThumbnails(save);
//        shopInformation.setUid(4);
            int uid = (int) request.getSession().getAttribute("uid");
            shopInformation.setUid(uid);
            try {
                int result = shopInformationService.insertSelective(shopInformation);
                //插入失败？？？
                if (result != 1) {
                    model.addAttribute("message", "请输入正确的格式!!!!!");
                    model.addAttribute("token", goodsToken);
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "layui_publish";
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "请输入正确的格式!!!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "layui_publish";
            }
            int sid = shopInformationService.selectIdByImage(image);// get the id which is belongs shopInformation
            //将发布的商品的编号插入到用户的发布中
            UserRelease userRelease = new UserRelease();
            userRelease.setModified(new Date());
            userRelease.setSid(sid);
            userRelease.setUid(uid);
            try {
                int result = userReleaseService.insertSelective(userRelease);
                //如果关联失败，删除对应的商品和商品图片
                if (result != 1) {
                    //if insert failure,transaction rollback.
                    shopInformationService.deleteByPrimaryKey(sid);
//                shopPictureService.deleteByPrimaryKey(spid);
                    model.addAttribute("token", goodsToken);
                    model.addAttribute("message", "请输入正确的格式!!!!!");
                    request.getSession().setAttribute("goodsToken", goodsToken);
                    return "layui_publish";
                }
            } catch (Exception e) {
                //if insert failure,transaction rollback.
                shopInformationService.deleteByPrimaryKey(sid);
                e.printStackTrace();
                model.addAttribute("token", goodsToken);
                model.addAttribute("message", "请输入正确的格式!!!!!");
                request.getSession().setAttribute("goodsToken", goodsToken);
                return "layui_publish";
            }
            shopInformation.setId(sid);
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("userInformation", userInformation);
            String sb = getSort(sort);
            model.addAttribute("sort", sb);
            model.addAttribute("action", 2);
            return "redirect:/layui_mypublish.do";  //返回到用户的发布界面，直接传了model
        } else if (action == 2) {//更新商品
            ShopInformation shopInformation = new ShopInformation();
            shopInformation.setModified(new Date());
            shopInformation.setQuantity(quantity);
            shopInformation.setSort(sort);
            shopInformation.setPrice(new BigDecimal(price));
            shopInformation.setRemark(remark);
            shopInformation.setImage(image);
            shopInformation.setLevel(level);
            shopInformation.setName(name);
            shopInformation.setId(id);
            try {
                int result = shopInformationService.updateByPrimaryKeySelective(shopInformation);
                if (result != 1) {
                    return "redirect:layui_publish.do";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:layui_publish.do";
            }
            goodsToken = TokenProccessor.getInstance().makeToken();
            request.getSession().setAttribute("goodsToken", goodsToken);
            model.addAttribute("token", goodsToken);
            shopInformation = shopInformationService.selectByPrimaryKey(id);
            model.addAttribute("userInformation", userInformation);
            model.addAttribute("shopInformation", shopInformation);
            model.addAttribute("action", 2);
            model.addAttribute("sort", getSort(sort));//这里还需要再做一个发布修改
        }
        return "redirect:/layui_mypublish.do";
    }


    //用户发布求购
    @RequestMapping(value = "/publishUserWant.do")
//    @ResponseBody
    public String publishUserWant(HttpServletRequest request, Model model,
                                  @RequestParam String name,
                                  @RequestParam int sort, @RequestParam int quantity,
                                  @RequestParam double price, @RequestParam String remark,
                                  @RequestParam String token, @RequestParam int action,@RequestParam(required = false ) int id) {
//        Map<String, Integer> map = new HashMap<>();
        //determine whether the user exits//
//        int action=1;
     //   int id=1;
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            //if the user no exits in the session,
//            map.put("result", 2);
            return "redirect:/layui_login.do";
        }
        String publishUserWantToke = (String) request.getSession().getAttribute("publishUserWantToken");
        if (StringUtils.getInstance().isNullOrEmpty(publishUserWantToke) || !publishUserWantToke.equals(token)) {
//            map.put("result", 2);
            return "redirect:layui_wishes.do?error=3";//还需要再前端显示错误的script的代码。
        } else {
            request.getSession().removeAttribute("publishUserWantToken");
        }
//        name = StringUtils.replaceBlank(name);
//        remark = StringUtils.replaceBlank(remark);
//        name = StringUtils.getInstance().txtReplace(name);
//        remark = StringUtils.getInstance().txtReplace(remark);
        try {
            if (name.length() < 1 || remark.length() < 1 || name.length() > 25 || remark.length() > 25) {
                return "redirect:layui_wishes.do";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:layui_wishes.do?error=1";
        }

        if(action==1){


            UserWant userWant = new UserWant();
            userWant.setModified(new Date());
            userWant.setName(name);
            userWant.setPrice(new BigDecimal(price));
            userWant.setQuantity(quantity);
            userWant.setRemark(remark);
            userWant.setUid((Integer) request.getSession().getAttribute("uid"));
            userWant.setSort(sort);
            int result;
            try {
                result = userWantService.insertSelective(userWant);
                if (result != 1) {
//                map.put("result", result);
                    return "redirect:/layui_wishes.do?error=2";
                }
            } catch (Exception e) {
                e.printStackTrace();
//            map.put("result", result);
                return "redirect:/layui_wishes.do?error=2";
            }
//        map.put("result", result);

        }
        if(action==2){
            UserWant userWant=userWantService.selectByPrimaryKey(id);
            userWant.setModified(new Date());
            userWant.setName(name);
            userWant.setPrice(new BigDecimal(price));
            userWant.setQuantity(quantity);
            userWant.setRemark(remark);
            userWant.setUid((Integer) request.getSession().getAttribute("uid"));
            userWant.setSort(sort);
            int result;
            try {
                result = userWantService.updateByPrimaryKeySelective(userWant);
                if (result != 1) {
//                map.put("result", result);
                    return "redirect:/layui_wishes.do?error=2";
                }
            } catch (Exception e) {
                e.printStackTrace();
//            map.put("result", result);
                return "redirect:/layui_wishes.do?error=2";
            }


        }

        return "redirect:/layui_mywishes.do";
    }

    //修改求购
    @RequestMapping(value = "/modified_require_product.do")
    public String modifiedRequireProduct(HttpServletRequest request, Model model,
                                         @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return "redirect:/layui_login.do";
        }
        String publishUserWantToken = TokenProccessor.getInstance().makeToken();
        request.getSession().setAttribute("publishUserWantToken", publishUserWantToken);
        model.addAttribute("token", publishUserWantToken);
        model.addAttribute("userInformation", userInformation);
     UserWant userWant = userWantService.selectByPrimaryKey(id);
        model.addAttribute("userWant", userWant);
//        String sort = getSort(userWant.getSort());
//        model.addAttribute("sort", sort);
           model.addAttribute("id",id);//不可以这样
        model.addAttribute("action",2);//哎呀，wish用的是0和1 publish用的1 和2 ~~~//我还是改回来吧
        return "layui_wishes";
    }

    //用户信息管理
    @RequestMapping(value = "/certification.do", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse certification(HttpServletRequest request,
                             @RequestParam(required = false) String userName,
                             @RequestParam(required = false) String realName,
                             @RequestParam(required = false) String clazz, @RequestParam String token,
                             @RequestParam(required = false) String sno, @RequestParam(required = false) String dormitory,
                             @RequestParam(required = false) String gender) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        Map<String, Integer> map = new HashMap<>();
        map.put("result", 0);
        //该用户还没有登录
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return BaseResponse.fail("用户未登录哦！");
        }
        String certificationToken = (String) request.getSession().getAttribute("personalInfoToken");
        //防止重复提交
//        boolean b = token.equals(certificationToken);
        if (StringUtils.getInstance().isNullOrEmpty(certificationToken)) {
            return BaseResponse.fail("重复提交！");
        } else {
            request.getSession().removeAttribute("certificationToken");
        }
        if (userName != null && userName.length() < 25) {
            userName = StringUtils.getInstance().replaceBlank(userName);
            userInformation.setUsername(userName);
        } else if (userName != null && userName.length() >= 25) {
            return BaseResponse.fail("用户名过长！");
        }
        if (realName != null && realName.length() < 25) {
            realName = StringUtils.getInstance().replaceBlank(realName);
            userInformation.setRealname(realName);
        } else if (realName != null && realName.length() >= 25) {
            return  BaseResponse.fail("真实名字过长！");
        }
        if (clazz != null && clazz.length() < 25) {
            clazz = StringUtils.getInstance().replaceBlank(clazz);
            userInformation.setClazz(clazz);
        } else if (clazz != null && clazz.length() >= 25) {
            return  BaseResponse.fail("班级名过长！");
        }
        if (sno != null && sno.length() < 25) {
            sno = StringUtils.getInstance().replaceBlank(sno);
            userInformation.setSno(sno);
        } else if (sno != null && sno.length() >= 25) {
            return  BaseResponse.fail("学号过长！");
        }
        if (dormitory != null && dormitory.length() < 25) {
            dormitory = StringUtils.getInstance().replaceBlank(dormitory);
            userInformation.setDormitory(dormitory);
        } else if (dormitory != null && dormitory.length() >= 25) {
            return  BaseResponse.fail("宿舍号过长！");
        }
        if (gender != null && gender.length() <= 2) {
            gender = StringUtils.getInstance().replaceBlank(gender);
            userInformation.setGender(gender);
        } else if (gender != null && gender.length() > 2) {
            return  BaseResponse.fail("性别选择错误！");
        }
        int result = userInformationService.updateByPrimaryKeySelective(userInformation);
        if (result != 1) {
            //更新失败，认证失败
            return  BaseResponse.fail("更新失败、数据库操作错误");
        }
        //认证成功
        request.getSession().setAttribute("userInformation", userInformation);
        map.put("result", 1);
        return  BaseResponse.success("修改成功！");
    }


    //about shopcart
    //删除购物车内容
    @RequestMapping(value = "/deleteShopCar.do")
    @ResponseBody
    public BaseResponse deleteShopCar(HttpServletRequest request, @RequestParam int id, @RequestParam int sid) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return BaseResponse.fail();
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(0);
        goodsCar.setId(id);
        goodsCar.setSid(sid);
        goodsCar.setUid(uid);
        int result = goodsCarService.updateByPrimaryKeySelective(goodsCar);
        if (result != 1) {
            return BaseResponse.fail();
        }
        return BaseResponse.success();
    }

    @RequestMapping(value = "/insertGoodsCar.do")
    @ResponseBody
    public BaseResponse insertGoodsCar(HttpServletRequest request, @RequestParam int id) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return new BaseResponse(2,"用户未登录");//和前台对应
        }
        int uid = userInformation.getId();
        GoodsCar goodsCar = new GoodsCar();
        goodsCar.setDisplay(1);
        goodsCar.setModified(new Date());
        goodsCar.setQuantity(1);
        goodsCar.setUid(uid);
        goodsCar.setSid(id);
        int result = goodsCarService.insertSelective(goodsCar);
        return BaseResponse.success();
    }

    @RequestMapping(value = "/publish_fromwish.do")
    @ResponseBody
    public BaseResponse wishestopublish(HttpServletRequest request) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return new BaseResponse(2,"用户未登录");//和前台对应
        }

        return BaseResponse.success();
    }

    @RequestMapping(value = "/buyShopCar.do")
    @ResponseBody
    public BaseResponse buyShopCar(HttpServletRequest request, @RequestParam(value ="Id[]")List<String> Id, @RequestParam(value ="Sid[]" )List<String> Sid,@RequestParam(value ="Num[]" ) List<String> Num) {
        UserInformation userInformation = (UserInformation) request.getSession().getAttribute("userInformation");
        if (StringUtils.getInstance().isNullOrEmpty(userInformation)) {
            return BaseResponse.fail();
        }
//       JSONArray Sid= JSONArray.parseArray(SidJ);
//        JSONArray Num= JSONArray.parseArray(NumJ);
//        for (int i = 0; i < ja.size(); i++) {
//
//            System.out.println(ja.getJSONObject(i).get("table"));
//
//        }
        int[] id =new int [Id.size()];
      //  int id=Integer.parseInt(Id);
        int uid = userInformation.getId();
        int[] sid=new int[Sid.size()];
        int[] num =new int [Num.size()];
        for(int i=0;i<Sid.size();i++){
            sid[i]=Integer.parseInt(Sid.get(i));
            num[i]=Integer.parseInt(Num.get(i));
            id[i]=Integer.parseInt(Id.get(i));
//             sid[i]=Sid.get(i);
//             num[i]=Num.get(i);
            GoodsCar goodsCar = new GoodsCar();
            goodsCar.setDisplay(0);
            goodsCar.setId(id[i]);
            goodsCar.setSid(sid[i]);
            goodsCar.setUid(uid);
            int result = goodsCarService.updateByPrimaryKeySelective(goodsCar);
            if (result != 1) {
                return BaseResponse.fail();
            }
            ShopInformation shopInformation=new ShopInformation();
            shopInformation=shopInformationService.selectByPrimaryKey(sid[i]);
            if(shopInformation.getQuantity()-num[i]>0){
                shopInformation.setQuantity(shopInformation.getQuantity()-num[i]);
                if(shopInformation.getQuantity()==0){
                    shopInformation.setDisplay(0);
                }
                int result1=shopInformationService.updateByPrimaryKeySelective(shopInformation);
                if (result1 != 1) {
                    return BaseResponse.fail();
                }
            }
        }



        return BaseResponse.success();
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



    //获取最详细的分类，第三层
    private Specific selectSpecificBySort(int sort) {
        return specificeService.selectByPrimaryKey(sort);
    }

    //获得第二层分类
    private Classification selectClassificationByCid(int cid) {
        return classificationService.selectByPrimaryKey(cid);
    }

    //获得第一层分类
    private AllKinds selectAllKindsByAid(int aid) {
        return allKindsService.selectByPrimaryKey(aid);
    }




}
