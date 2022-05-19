package com.bjpowernode.crm.workbench.web.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.bjpowernode.crm.commons.contants.Contants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.util.DateUtil;
import com.bjpowernode.crm.commons.util.HSSFUtils;
import com.bjpowernode.crm.commons.util.UUIDUtil;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        List<User> users = userService.selectUserList();
        request.setAttribute("userList",users);
        return "workbench/activity/index";
    }
    @RequestMapping("/workbench/activity/insertActivity.do")
    @ResponseBody
    public Object insertActivity(Activity activity, HttpSession session){
        String id = UUIDUtil.getUUID();
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        String createTime = DateUtil.forMateDateTime(new Date());
        activity.setId(id);
        activity.setCreateTime(createTime);
        activity.setCreateBy(user.getName());

        ReturnObject ro = new ReturnObject();

        System.out.println(1);
        System.out.println(activity);

        try{
            int count = activityService.insertActivity(activity);

            if (count>0){
                ro.setCode("1");
            }else{
                ro.setCode("0");
                ro.setMessage("系统忙，请稍后。。。");
            }
        }catch (Exception e){
            e.printStackTrace();
            ro.setCode("0");
            ro.setMessage("系统忙，请稍后。。。");
        }
        return ro;
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name,String owner,String startDate,String endDate,
                                                  int pageNo,int pageSize){
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int count = activityService.queryCountByCondition(map);

        Map<String,Object> retMap = new HashMap<>();
        retMap.put("activityList",activityList);
        retMap.put("count",count);
        return retMap;

    }
    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public Object deleteActivityByIds(String[] id){
        ReturnObject ro = new ReturnObject();
        try{
            int ret = activityService.deleteActivityByIds(id);
            if (ret>0){
                ro.setCode("1");
            }else{
                ro.setCode("0");
                ro.setMessage("系统忙，请稍后。。。");
            }
        }catch (Exception e){
            e.printStackTrace();
            ro.setCode("0");
            ro.setMessage("系统忙，请稍后。。。");
        }
        return ro;
    }

    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id){
        Activity activity = activityService.queryActivityById(id);
        return activity;
    }

    @RequestMapping("/workbench/activity/updateByPrimaryKeySelective.do")
    @ResponseBody
    public Object updateByPrimaryKeySelective(Activity activity,HttpSession session){
        User user = (User)session.getAttribute(Contants.SESSION_USER);

        activity.setEditBy(user.getName());
        activity.setEditTime(DateUtil.forMateDateTime(new Date()));

        ReturnObject ro = new ReturnObject();
        try{
            int ret = activityService.updateByPrimaryKeySelective(activity);
            if (ret>0){
                ro.setCode("1");
                ro.setMessage("更新成功！");
            }else{
                ro.setCode("0");
                ro.setMessage("系统忙，请稍后。。。");
            }
        }catch (Exception e){
            e.printStackTrace();
            ro.setCode("0");
            ro.setMessage("系统忙，请稍后。。。");
        }
        return ro;
    }

    @RequestMapping("/workbench/activity/exportAllActivities.do")
    public void exportAllActivities(HttpServletResponse response) throws Exception{
        List<Activity> activities = activityService.selectAllActivities();

        //使用iop创建excel文件
        HSSFWorkbook hf = new HSSFWorkbook();
        HSSFSheet sheet = hf.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("描述");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");

        if (activities!=null && activities.size()>0){
            Activity activity = null;
            for (int i = 0;i<activities.size();i++){
                activity=activities.get(i);

                row = sheet.createRow(i+1);
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());

            }
        }

        //根据web对象生成excel文件
        /*OutputStream os = new FileOutputStream("D:\\ExcelText\\activitiyList.xls");

        hf.write(os);
        os.close();
        hf.close();*/

        //把生成的excel文件下载到客户段
        //1、设置文件格式,设置响应头信息
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=activityList.xls");
        //2.设置响应输出流
        OutputStream out = response.getOutputStream();
        //3.读取文件
        /*InputStream is = new FileInputStream("D:\\ExcelText\\activityList.xls");
        byte[] buff = new byte[256];
        int len = 0;
        while((len=is.read(buff))!=-1){
            out.write(buff,0,len);
        }
        //4.关闭对应流
        is.close();
        out.flush();*/

        hf.write(out);
        out.flush();
        hf.close();

    }

    @RequestMapping("/workbench/activity/exportAllActivitiesById.do")
    public void exportAllActivitiesById(String[] id,HttpServletResponse response) throws Exception{
        List<Activity> activities = activityService.selectAllActivitiesById(id);

        //使用iop创建excel文件
        HSSFWorkbook hf = new HSSFWorkbook();
        HSSFSheet sheet = hf.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell=row.createCell(1);
        cell.setCellValue("所有者");
        cell=row.createCell(2);
        cell.setCellValue("名称");
        cell=row.createCell(3);
        cell.setCellValue("开始日期");
        cell=row.createCell(4);
        cell.setCellValue("结束日期");
        cell=row.createCell(5);
        cell.setCellValue("成本");
        cell=row.createCell(6);
        cell.setCellValue("描述");
        cell=row.createCell(7);
        cell.setCellValue("创建时间");
        cell=row.createCell(8);
        cell.setCellValue("创建者");
        cell=row.createCell(9);
        cell.setCellValue("修改时间");
        cell=row.createCell(10);
        cell.setCellValue("修改者");

        if (activities!=null && activities.size()>0){
            Activity activity = null;
            for (int i = 0;i<activities.size();i++){
                activity=activities.get(i);

                row = sheet.createRow(i+1);
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell=row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell=row.createCell(2);
                cell.setCellValue(activity.getName());
                cell=row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell=row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell=row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell=row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell=row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell=row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell=row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell=row.createCell(10);
                cell.setCellValue(activity.getEditBy());

            }
        }

        //根据web对象生成excel文件
        /*OutputStream os = new FileOutputStream("D:\\ExcelText\\activitiyList.xls");

        hf.write(os);
        os.close();
        hf.close();*/

        //把生成的excel文件下载到客户段
        //1、设置文件格式,设置响应头信息
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=activitiyList.xls");
        //2.设置响应输出流
        OutputStream out = response.getOutputStream();
        //3.读取文件
        /*InputStream is = new FileInputStream("D:\\ExcelText\\activitiyList.xls");
        byte[] buff = new byte[256];
        int len = 0;
        while((len=is.read(buff))!=-1){
            out.write(buff,0,len);
        }
        //4.关闭对应流
        is.close();
        out.flush();*/

        hf.write(out);
        out.flush();
        hf.close();

    }
    @RequestMapping("/workbench/activity/insertActivityByList.do")
    @ResponseBody
    public Object insertActivityByList(MultipartFile activityFile,HttpSession session){
        ReturnObject ro = new ReturnObject();
        try{
            /*//把excel文件写进磁盘目录中
            String activityName = activityFile.getOriginalFilename();
            File file = new File("D:\\ExcelText\\",activityName);
            activityFile.transferTo(file);

            FileInputStream is = new FileInputStream("D:\\ExcelText\\"+activityName);*/
            //优化效率
            InputStream is = activityFile.getInputStream();
            HSSFWorkbook wb = new HSSFWorkbook(is);

            HSSFSheet sheetAt = wb.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;

            List<Activity> activityList = new ArrayList();

            for (int i = 1;i<=sheetAt.getLastRowNum();i++){
                activity = new Activity();
                activity.setId(UUIDUtil.getUUID());
                User user = (User) session.getAttribute(Contants.SESSION_USER);
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtil.forMateDateTime(new Date()));
                activity.setCreateBy(user.getName());

                row = sheetAt.getRow(i);
                for (int j = 0;j<row.getLastCellNum();j++){
                    cell = row.getCell(j);
                    String cellValue = HSSFUtils.getCellValueForString(cell);
                    if (j==0){
                        activity.setName(cellValue);
                    }else if (j==1){
                        activity.setStartDate(cellValue);
                    }else if (j==2){
                        activity.setEndDate(cellValue);
                    }else if (j==3){
                        activity.setCost(cellValue);
                    }else if (j==4){
                        activity.setDescription(cellValue);
                    }
                }


                activityList.add(activity);
            }

            int i = activityService.insertActivityByList(activityList);

            ro.setCode("1");
            ro.setRetData(i);

        }catch (Exception e){
            e.printStackTrace();
            ro.setCode("0");
            ro.setMessage("系统忙请稍后。。。");
        }
        return ro;
    }
    @RequestMapping("/workbench/activity/queryActivityRemarkById.do")
    public String queryActivityRemarkById(String id,HttpServletRequest request){
        Activity activity = activityService.queryActivityDetailById(id);
        List<ActivityRemark> activityRemarkList = activityRemarkService.selectActivityRemarkById(id);

        request.setAttribute("activity",activity);
        request.setAttribute("activityRemarkList",activityRemarkList);

        return "workbench/activity/detail";
    }

    @RequestMapping("/workbench/activity/insertActivityRemark.do")
    @ResponseBody
    public Object insertActivityRemark(ActivityRemark activityRemark, HttpSession session){
        User user = (User)session.getAttribute(Contants.SESSION_USER);
        activityRemark.setId(UUIDUtil.getUUID());
        activityRemark.setCreateTime(DateUtil.forMateDateTime(new Date()));
        activityRemark.setCreateBy(user.getId());
        activityRemark.setEditFlag("0");

        ReturnObject ro = new ReturnObject();
        try{
            int count = activityRemarkService.insertActivityRemark(activityRemark);
            if (count>0){
                ro.setCode("1");
                ro.setRetData(activityRemark);
            }else{
                ro.setCode("0");
                ro.setMessage("系统忙，请稍后。。。。");
            }
        }catch (Exception e){
            e.printStackTrace();
            ro.setCode("0");
            ro.setMessage("系统忙，请稍后。。。。");
        }
        return ro;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public Object deleteActivityRemarkById(String id){
        int ret = activityRemarkService.deleteActivityRemarkById(id);
        ReturnObject ro = new ReturnObject();
        if (ret >0){
            ro.setCode("1");
        }else {
            ro.setCode("0");
            ro.setMessage("系统忙，请稍后。。。。");
        }
        return ro;
    }
    @RequestMapping("/workbench/activity/updateActivityRemarkById.do")
    @ResponseBody
    public Object updateActivityRemarkById(ActivityRemark activityRemark,HttpSession session){
        User user = (User)session.getAttribute(Contants.SESSION_USER);
        activityRemark.setEditTime(DateUtil.forMateDateTime(new Date()));
        activityRemark.setEditBy(user.getId());
        activityRemark.setEditFlag("1");

        ReturnObject ro = new ReturnObject();
        try{
            int ret = activityRemarkService.updateActivityRemarkById(activityRemark);
            if (ret>0){
                ro.setCode("1");
                ro.setRetData(activityRemark);
            }else{
                ro.setRetData("0");
                ro.setMessage("系统忙，请稍后。。。。");
            }
        }catch (Exception e){
            e.printStackTrace();
            ro.setRetData("0");
            ro.setMessage("系统忙，请稍后。。。。");
        }
        return ro;
    }
}
