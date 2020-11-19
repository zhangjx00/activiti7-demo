package com.activiti7;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Part4_Task {

    @Autowired
    private TaskService taskService;

    /**
     * 获取所有任务列表
     */
    @Test
    public void getTasks(){

        /**
         * select distinct RES.* from ACT_RU_TASK RES order by RES.ID_ asc LIMIT 2147483647 OFFSET 0
         */
        List<Task> list = taskService.createTaskQuery().list();
        for(Task tk : list){
            System.out.println("Id："+tk.getId());
            System.out.println("Name："+tk.getName());
            System.out.println("Assignee："+tk.getAssignee());
        }
    }

    /**
     * 查询我的代办任务
     */
    @Test
    public void getTasksByAssignee(){

        /**
         * select distinct RES.* from ACT_RU_TASK RES WHERE RES.ASSIGNEE_ = 'bajie' order by RES.ID_ asc LIMIT 2147483647 OFFSET 0
         */
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("bajie")
                .list();
        for(Task tk : list){
            System.out.println("Id："+tk.getId());
            System.out.println("Name："+tk.getName());
            System.out.println("Assignee："+tk.getAssignee());
        }

    }

    /**
     * 执行任务
     */
    @Test
    public void completeTask(){

        /**
         * 1、 历史节点表
         * insert into ACT_HI_ACTINST ( ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values ( 'aefc9a95-2a12-11eb-874a-6ab26bb3215c', 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', '86039de2-2a0c-11eb-b436-a6ee3527a6d6', '86041313-2a0c-11eb-b436-a6ee3527a6d6', '_3', NULL, NULL, 'EndEvent', 'endEvent', NULL, '2020-11-19T10:55:29.850+0800', '2020-11-19T10:55:29.852+0800', 2, NULL, '' )
         *
         * 2、 历史任务实例表
         * update ACT_HI_TASKINST set PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', EXECUTION_ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6', NAME_ = 'UserTask', PARENT_TASK_ID_ = NULL, DESCRIPTION_ = NULL, OWNER_ = NULL, ASSIGNEE_ = 'bajie', CLAIM_TIME_ = NULL, END_TIME_ = '2020-11-19T10:55:29.817+0800', DURATION_ = 2645659, DELETE_REASON_ = NULL, TASK_DEF_KEY_ = '_4', FORM_KEY_ = NULL, PRIORITY_ = 50, DUE_DATE_ = NULL, CATEGORY_ = NULL where ID_ = '86080ab6-2a0c-11eb-b436-a6ee3527a6d6'
         *
         * 3、 历史流程实例表
         * update ACT_HI_PROCINST set PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', BUSINESS_KEY_ = 'bKey002', START_TIME_ = '2020-11-19T10:11:24.129+0800', END_TIME_ = '2020-11-19T10:55:29.926+0800', DURATION_ = 2645797, END_ACT_ID_ = '_3', DELETE_REASON_ = NULL, NAME_ = NULL where ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6'
         *
         * 4、 运行时流程执行实例表
         * update ACT_RU_EXECUTION set REV_ = 8, BUSINESS_KEY_ = NULL, PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', ACT_ID_ = '_3', IS_ACTIVE_ = false, IS_CONCURRENT_ = false, IS_SCOPE_ = false, IS_EVENT_SCOPE_ = false, IS_MI_ROOT_ = false, PARENT_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUPER_EXEC_ = NULL, ROOT_PROC_INST_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUSPENSION_STATE_ = 1, NAME_ = NULL, IS_COUNT_ENABLED_ = false, EVT_SUBSCR_COUNT_ = 0, TASK_COUNT_ = 0, JOB_COUNT_ = 0, TIMER_JOB_COUNT_ = 0, SUSP_JOB_COUNT_ = 0, DEADLETTER_JOB_COUNT_ = 0, VAR_COUNT_ = 0, ID_LINK_COUNT_ = 0 where ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 7
         * update ACT_RU_EXECUTION set REV_ = 8, BUSINESS_KEY_ = 'bKey002', PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', ACT_ID_ = NULL, IS_ACTIVE_ = false, IS_CONCURRENT_ = false, IS_SCOPE_ = true, IS_EVENT_SCOPE_ = false, IS_MI_ROOT_ = false, PARENT_ID_ = NULL, SUPER_EXEC_ = NULL, ROOT_PROC_INST_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUSPENSION_STATE_ = 1, NAME_ = NULL, IS_COUNT_ENABLED_ = false, EVT_SUBSCR_COUNT_ = 0, TASK_COUNT_ = 0, JOB_COUNT_ = 0, TIMER_JOB_COUNT_ = 0, SUSP_JOB_COUNT_ = 0, DEADLETTER_JOB_COUNT_ = 0, VAR_COUNT_ = 0, ID_LINK_COUNT_ = 0 where ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 7
         *
         * 5、 历史节点表
         * update ACT_HI_ACTINST set EXECUTION_ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6', ASSIGNEE_ = 'bajie', END_TIME_ = '2020-11-19T10:55:29.846+0800', DURATION_ = 2645706, DELETE_REASON_ = NULL where ID_ = '86052485-2a0c-11eb-b436-a6ee3527a6d6'
         *
         * 6、 运行时用户信息表
         * delete from ACT_RU_IDENTITYLINK where ID_ = '860858d7-2a0c-11eb-b436-a6ee3527a6d6'
         *
         * 7、 运行任务信息表
         * delete from ACT_RU_TASK where ID_ = '86080ab6-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 7
         *
         * 8、 运行流程执行实例表
         * delete from ACT_RU_EXECUTION where ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 8
         * delete from ACT_RU_EXECUTION where ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 8
         */

        taskService.complete("86080ab6-2a0c-11eb-b436-a6ee3527a6d6");
        System.out.println("完成任务");

    }

    /**
     * 拾取任务
     */
    @Test
    public void claimTask(){
        Task task = taskService.createTaskQuery().taskId("1f2a8edf-cefa-11ea-84aa-dcfb4875e032").singleResult();
        taskService.claim("1f2a8edf-cefa-11ea-84aa-dcfb4875e032","bajie");
    }

    /**
     * 归还与交办任务
     */
    @Test
    public void setTaskAssignee(){
        Task task = taskService.createTaskQuery().taskId("1f2a8edf-cefa-11ea-84aa-dcfb4875e032").singleResult();
        taskService.setAssignee("1f2a8edf-cefa-11ea-84aa-dcfb4875e032","null");//归还候选任务
        taskService.setAssignee("1f2a8edf-cefa-11ea-84aa-dcfb4875e032","wukong");//交办
    }



}
