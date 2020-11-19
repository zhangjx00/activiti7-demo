package com.activiti7.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 任务监听器2。
 * 获取上一步中存储的执行人
 */
@Slf4j
public class TkListener2 implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        //获取上一步中存储的执行人
        log.info("执行人2："+delegateTask.getVariable("delegateAssignee"));

        //根据执行人username获取组织机构代码，加工后得到领导是wukong，设置执行人是wukong，wukong登录后可以看到这条数据
        delegateTask.setAssignee("wukong");

        //还可以设置候选人和候选组
        // delegateTask.addCandidateUser("");
        // delegateTask.addCandidateGroup("");
    }
}
