package com.activiti7.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 任务监听器1。发送短信
 *
 * 任务监听器能拿到任务相关数据，
 * 执行监听器拿到流程相关数据。执行监听器通常用于存储读取变量、处理业务信息
 */
@Slf4j
public class TkListener1 implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("执行人：{}", delegateTask.getAssignee());

        //根据用户名查询用户电话并调用发送短信接口
        /**
         * 把用户名作为参数存起来，
         */
        delegateTask.setVariable("delegateAssignee",delegateTask.getAssignee());
    }
}
