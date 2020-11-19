package com.activiti7;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Part5_HistoricTaskInstance {
    @Autowired
    private HistoryService historyService;

    /**
     * 根据用户名查询历史记录
     */
    @Test
    public void HistoricTaskInstanceByUser(){

        /**
         * select distinct RES.* from ACT_HI_TASKINST RES WHERE RES.ASSIGNEE_ = 'bajie' order by RES.END_TIME_ asc LIMIT 2147483647 OFFSET 0
         */
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .taskAssignee("bajie")
                .list();
        for(HistoricTaskInstance hi : list){
            System.out.println("Id："+ hi.getId());
            System.out.println("ProcessInstanceId："+ hi.getProcessInstanceId());
            System.out.println("Name："+ hi.getName());

        }

    }


    /**
     * 根据流程实例ID查询历史
     */
    @Test
    public void HistoricTaskInstanceByPiID(){

        /**
         * select distinct RES.* from ACT_HI_TASKINST RES WHERE RES.PROC_INST_ID_ = 'fcb15608-2998-11eb-8371-7226adc5313f' order by RES.END_TIME_ asc LIMIT 2147483647 OFFSET 0
         */
        List<HistoricTaskInstance> list = historyService
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceEndTime().asc()
                .processInstanceId("fcb15608-2998-11eb-8371-7226adc5313f")
                .list();
        for(HistoricTaskInstance hi : list){
            System.out.println("Id："+ hi.getId());
            System.out.println("ProcessInstanceId："+ hi.getProcessInstanceId());
            System.out.println("Name："+ hi.getName());

        }
    }
}
