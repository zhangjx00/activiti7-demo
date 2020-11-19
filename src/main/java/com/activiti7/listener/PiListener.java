package com.activiti7.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 执行监听器，执行监听器能拿到流程相关数据
 *
 * 记录审批时间
 */
@Slf4j
public class PiListener implements ExecutionListener {
    @Autowired

    //和流程图中保持一致：sendType
    private Expression sendType;
    @Override
    public void notify(DelegateExecution execution) {
        log.info(execution.getEventName());
        log.info(execution.getProcessDefinitionId());

        if("start".equals(execution.getEventName())){
            //记录节点开始时间，存到表中
        }else if("end".equals(execution.getEventName())){
            //记录节点结束时间，存到表中
        }
        log.info("sendType:", sendType.getValue(execution).toString());

    }
}
