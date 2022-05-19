package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    int insertActivity(Activity activity);

    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    int queryCountByCondition(Map<String,Object> map);

    int deleteActivityByIds(String[] ids);

    Activity queryActivityById(String id);

    int updateByPrimaryKeySelective(Activity activity);

    List<Activity> selectAllActivities();

    List<Activity> selectAllActivitiesById(String[] ids);

    int insertActivityByList(List<Activity> activityList);

    Activity queryActivityDetailById(String id);

    List<Activity> selectActivityForDetailByClueId(String id);

    List<Activity> selectActivityListByName(String activityName);

}
