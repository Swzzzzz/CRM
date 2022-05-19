package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.util.DateUtil;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/settings/qx/user/toLogin.do")
    public String toLogin(){
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object login(String loginPwd, String loginAct, String isRemPwd, HttpServletRequest request,
                        HttpServletResponse response, HttpSession session){
        Map<String,Object> map = new HashMap<>();
        map.put("loginPwd",loginPwd);
        map.put("loginAct",loginAct);

        User user = userService.queryUserByLoginActAndPwd(map);

        ReturnObject ro = new ReturnObject();

        if (user==null){
            ro.setCode("0");
            ro.setMessage("账号或密码错误");
        }else {
            String nowDate = DateUtil.forMateDateTime(new Date());
            System.out.println(nowDate);
            System.out.println(user);
            if (nowDate.compareTo(user.getExpireTime())>0){
                //已经过期
                ro.setCode("0");
                ro.setMessage("账号已经过期");
            }else if("0".equals(user.getLockState())){
                //以上锁
                ro.setCode("0");
                ro.setMessage("账号已以被锁定");
            //request.getRemoteAddr()获取访问用户的IP;
            }else if(!user.getAllowIps().contains(request.getRemoteAddr())){
                ro.setCode("0");
                ro.setMessage("账号IP受限");
            }else {
                ro.setCode("1");
                //将user对象存到session作用域中
                session.setAttribute(Contants.SESSION_USER,user);

                //实现记住密码，十天免登录
                if ("true".equals(isRemPwd)){
                    Cookie c1 = new Cookie("loginPwd",user.getLoginPwd());
                    c1.setMaxAge(10*24*60*60);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginAct",user.getLoginAct());
                    c2.setMaxAge(10*24*60*60);
                    response.addCookie(c2);

                }else{
                    Cookie c1 = new Cookie("loginPwd",user.getLoginPwd());
                    c1.setMaxAge(0);
                    response.addCookie(c1);
                    Cookie c2 = new Cookie("loginAct",user.getLoginAct());
                    c2.setMaxAge(0);
                    response.addCookie(c2);
                }

            }
        }
        return ro;
    }
    @RequestMapping("/settings/qs/user/logout.do")
    public String logout(HttpServletResponse response, HttpSession session){
        //消除cookie
        Cookie c1 = new Cookie("loginPwd","0");
        c1.setMaxAge(0);
        response.addCookie(c1);
        Cookie c2 = new Cookie("loginAct","1");
        c2.setMaxAge(0);
        response.addCookie(c2);
        //消除session
        session.invalidate();

        return "redirect:/";

    }
}
