package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class TranController {

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private CustomerService customerService;

    @RequestMapping("/workbench/transaction/index.do")
    public String toIndex(HttpServletRequest request){
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");

        request.setAttribute("stageList",stageList);
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);

        return "workbench/transaction/index";

    }

    @RequestMapping("/workbench/transaction/save.do")
    public String save(HttpServletRequest request){
        List<User> userList = userService.selectUserList();
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");

        request.setAttribute("userList",userList);
        request.setAttribute("stageList",stageList);
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);

        return "workbench/transaction/save";

    }
    @RequestMapping("/workbench/transaction/selectActivityListByName.do")
    @ResponseBody
    public Object selectActivityListByName(String activityName){
        List<Activity> activityList = activityService.selectActivityListByName(activityName);

        return activityList;
    }

    @RequestMapping("/workbench/transaction/queryPossibilityByStage.do")
    @ResponseBody
    public Object queryPossibilityByStage(String stage){
        ResourceBundle rb = ResourceBundle.getBundle("possibility");
        String possibility = rb.getString(stage);
        return possibility;
    }

    @RequestMapping("/workbench/transaction/selectActivityNameListByName.do")
    @ResponseBody
    public Object selectActivityNameListByName(String name){
        List<String> nameList = customerService.selectActivityNameListByName(name);

        return nameList;

    }
}
