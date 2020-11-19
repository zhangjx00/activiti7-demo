package com.activiti7;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class Part3_ProcessInstance {

    private static final String PROCESS_DEFINITION_KEY =  "Process_1";
    private static final String BUSINESS_KEY =  "bKey002";

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 初始化流程实例
     */
    @Test
    public void initProcessInstance(){

        /**
         * startProcessInstanceByKey
         * 第一个参数： 流程图ID，即ACT_RE_PROCDEF 中 key_ 列获取数据
         * 第二个参数： 业务ID。用于关联关联业务数据，如报销、请假数据等
         *
         * 数据库操作：
         * 1、select * from ACT_RE_PROCDEF where KEY_ = ? and (TENANT_ID_ = '' or TENANT_ID_ is null) and VERSION_ = (select max(VERSION_) from ACT_RE_PROCDEF where KEY_ = ? and (TENANT_ID_ = '' or TENANT_ID_ is null))
         *
         * 2、ACT_HI_TASKINST 历史任务实例表。
         * insert into ACT_HI_TASKINST ( ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, OWNER_, ASSIGNEE_, START_TIME_, CLAIM_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TASK_DEF_KEY_, FORM_KEY_, PRIORITY_, DUE_DATE_, CATEGORY_, TENANT_ID_ ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
         *
         * 3、ACT_HI_PROCINST 历史流程实例表：
         * insert into ACT_HI_PROCINST ( ID_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, START_TIME_, END_TIME_, DURATION_, START_USER_ID_, START_ACT_ID_, END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_, DELETE_REASON_, TENANT_ID_, NAME_ ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
         *
         * 4、ACT_HI_ACTINST 历史节点表：
         * insert into ACT_HI_ACTINST ( ID_, PROC_DEF_ID_, PROC_INST_ID_, EXECUTION_ID_, ACT_ID_, TASK_ID_, CALL_PROC_INST_ID_, ACT_NAME_, ACT_TYPE_, ASSIGNEE_, START_TIME_, END_TIME_, DURATION_, DELETE_REASON_, TENANT_ID_ ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
         *
         * 5、ACT_HI_IDENTITYLINK 历史流程用户信息表：
         * insert into ACT_HI_IDENTITYLINK (ID_, TYPE_, USER_ID_, GROUP_ID_, TASK_ID_, PROC_INST_ID_) values (?, ?, ?, ?, ?, ?)
         *
         * 6、ACT_RU_EXECUTION 运行时流程执行实例表：
         * insert into ACT_RU_EXECUTION (ID_, REV_, PROC_INST_ID_, BUSINESS_KEY_, PROC_DEF_ID_, ACT_ID_, IS_ACTIVE_, IS_CONCURRENT_, IS_SCOPE_,IS_EVENT_SCOPE_, IS_MI_ROOT_, PARENT_ID_, SUPER_EXEC_, ROOT_PROC_INST_ID_, SUSPENSION_STATE_, TENANT_ID_, NAME_, START_TIME_, START_USER_ID_, IS_COUNT_ENABLED_, EVT_SUBSCR_COUNT_, TASK_COUNT_, JOB_COUNT_, TIMER_JOB_COUNT_, SUSP_JOB_COUNT_, DEADLETTER_JOB_COUNT_, VAR_COUNT_, ID_LINK_COUNT_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
         *
         * 7、ACT_RU_TASK 运行时任务信息表：
         * insert into ACT_RU_TASK (ID_, REV_, NAME_, PARENT_TASK_ID_, DESCRIPTION_, PRIORITY_, CREATE_TIME_, OWNER_, ASSIGNEE_, DELEGATION_, EXECUTION_ID_, PROC_INST_ID_, PROC_DEF_ID_, TASK_DEF_KEY_, DUE_DATE_, CATEGORY_, SUSPENSION_STATE_, TENANT_ID_, FORM_KEY_, CLAIM_TIME_) values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
         *
         * 8、ACT_RU_IDENTITYLINK 运行时用户信息表
         * insert into ACT_RU_IDENTITYLINK (ID_, REV_, TYPE_, USER_ID_, GROUP_ID_, TASK_ID_, PROC_INST_ID_, PROC_DEF_ID_) values (?, 1, ?, ?, ?, ?, ?, ?)
         *
         */
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, BUSINESS_KEY);

        /**
         * 插入数据：
         * ACT_RU_EXECUTION 运行时流程执行实例表
         * ACT_RU_IDENTITYLINK  运行时用户信息表：存放下一个审批人
         */
        log.info("流程实例ID：{}", processInstance.getProcessDefinitionId());

    }


    /**
     * 获取流程实例列表
     */
    @Test
    public void getProcessInstances(){

        /**
         * select distinct RES.* , P.KEY_ as ProcessDefinitionKey, P.ID_ as ProcessDefinitionId, P.NAME_ as ProcessDefinitionName, P.VERSION_ as ProcessDefinitionVersion, P.DEPLOYMENT_ID_ as DeploymentId, S.PROC_INST_ID_ AS PARENT_PROC_INST_ID_
         * from ACT_RU_EXECUTION RES inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_ left outer join ACT_RU_EXECUTION S on RES.SUPER_EXEC_ = S.ID_
         * WHERE RES.PARENT_ID_ is null order by RES.ID_ asc LIMIT ? OFFSET ?
         */
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();

        log.info("实例数量：{}", list.size());
        for(ProcessInstance pi : list){
            log.info("--------流程实例------");
            log.info("ProcessInstanceId：{}", pi.getProcessInstanceId());
            log.info("ProcessDefinitionId：{}", pi.getProcessDefinitionId());
            log.info("isEnded：{}", pi.isEnded());
            log.info("isSuspended：{}", pi.isSuspended());
        }
    }


    /**
     * 暂停流程实例
     *
     * 修改表 ACT_RU_EXECUTION 中 SUSPENSION_STATE_ = 2
     */
    @Test
    public void suspendProcessInstance(){


        /**
         * 1、更新 ACT_RU_EXECUTION，SUSPENSION_STATE_ = 2 、REV_值加1，本流程有两条数据：
         * update ACT_RU_EXECUTION set REV_ = 4, BUSINESS_KEY_ = NULL, PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', ACT_ID_ = '_4', IS_ACTIVE_ = true, IS_CONCURRENT_ = false, IS_SCOPE_ = false, IS_EVENT_SCOPE_ = false, IS_MI_ROOT_ = false, PARENT_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUPER_EXEC_ = NULL, ROOT_PROC_INST_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUSPENSION_STATE_ = 2, NAME_ = NULL, IS_COUNT_ENABLED_ = false, EVT_SUBSCR_COUNT_ = 0, TASK_COUNT_ = 0, JOB_COUNT_ = 0, TIMER_JOB_COUNT_ = 0, SUSP_JOB_COUNT_ = 0, DEADLETTER_JOB_COUNT_ = 0, VAR_COUNT_ = 0, ID_LINK_COUNT_ = 0 where ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 3
         * update ACT_RU_EXECUTION set REV_ = 4, BUSINESS_KEY_ = 'bKey002', PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', ACT_ID_ = NULL, IS_ACTIVE_ = true, IS_CONCURRENT_ = false, IS_SCOPE_ = true, IS_EVENT_SCOPE_ = false, IS_MI_ROOT_ = false, PARENT_ID_ = NULL, SUPER_EXEC_ = NULL, ROOT_PROC_INST_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUSPENSION_STATE_ = 2, NAME_ = NULL, IS_COUNT_ENABLED_ = false, EVT_SUBSCR_COUNT_ = 0, TASK_COUNT_ = 0, JOB_COUNT_ = 0, TIMER_JOB_COUNT_ = 0, SUSP_JOB_COUNT_ = 0, DEADLETTER_JOB_COUNT_ = 0, VAR_COUNT_ = 0, ID_LINK_COUNT_ = 0 where ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 3
         *
         * 1、更新 ACT_RU_TASK，REV_值加1
         * update ACT_RU_TASK SET REV_ = 4, NAME_ = 'UserTask', PARENT_TASK_ID_ = NULL, PRIORITY_ = 50, CREATE_TIME_ = '2020-11-19T10:11:24.140+0800', OWNER_ = NULL, ASSIGNEE_ = 'bajie', DELEGATION_ = NULL, EXECUTION_ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6', PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', DESCRIPTION_ = NULL, DUE_DATE_ = NULL, CATEGORY_ = NULL, SUSPENSION_STATE_ = 2, FORM_KEY_ = NULL, CLAIM_TIME_ = NULL where ID_= '86080ab6-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 3
         */
        runtimeService.suspendProcessInstanceById("86039de2-2a0c-11eb-b436-a6ee3527a6d6");
        log.info("挂起流程实例");

    }

    /**
     * 激活流程实例
     *
     * 修改表 ACT_RU_EXECUTION 中 SUSPENSION_STATE_ = 1
     */
    @Test
    public void activitieProcessInstance(){

        //拷贝上面方法打印的流程ID

        /**
         * 1、更新 ACT_RU_EXECUTION，SUSPENSION_STATE_ = 1 、REV_值加1，本流程有两条数据：
         * update ACT_RU_EXECUTION set REV_ = 3, BUSINESS_KEY_ = NULL, PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', ACT_ID_ = '_4', IS_ACTIVE_ = true, IS_CONCURRENT_ = false, IS_SCOPE_ = false, IS_EVENT_SCOPE_ = false, IS_MI_ROOT_ = false, PARENT_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUPER_EXEC_ = NULL, ROOT_PROC_INST_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUSPENSION_STATE_ = 1, NAME_ = NULL, IS_COUNT_ENABLED_ = false, EVT_SUBSCR_COUNT_ = 0, TASK_COUNT_ = 0, JOB_COUNT_ = 0, TIMER_JOB_COUNT_ = 0, SUSP_JOB_COUNT_ = 0, DEADLETTER_JOB_COUNT_ = 0, VAR_COUNT_ = 0, ID_LINK_COUNT_ = 0 where ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 2
         * update ACT_RU_EXECUTION set REV_ = 3, BUSINESS_KEY_ = 'bKey002', PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', ACT_ID_ = NULL, IS_ACTIVE_ = true, IS_CONCURRENT_ = false, IS_SCOPE_ = true, IS_EVENT_SCOPE_ = false, IS_MI_ROOT_ = false, PARENT_ID_ = NULL, SUPER_EXEC_ = NULL, ROOT_PROC_INST_ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6', SUSPENSION_STATE_ = 1, NAME_ = NULL, IS_COUNT_ENABLED_ = false, EVT_SUBSCR_COUNT_ = 0, TASK_COUNT_ = 0, JOB_COUNT_ = 0, TIMER_JOB_COUNT_ = 0, SUSP_JOB_COUNT_ = 0, DEADLETTER_JOB_COUNT_ = 0, VAR_COUNT_ = 0, ID_LINK_COUNT_ = 0 where ID_ = '86039de2-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 2
         *
         * 1、更新 ACT_RU_TASK，REV_值加1
         * update ACT_RU_TASK SET REV_ = 3, NAME_ = 'UserTask', PARENT_TASK_ID_ = NULL, PRIORITY_ = 50, CREATE_TIME_ = '2020-11-19T10:11:24.140+0800', OWNER_ = NULL, ASSIGNEE_ = 'bajie', DELEGATION_ = NULL, EXECUTION_ID_ = '86041313-2a0c-11eb-b436-a6ee3527a6d6', PROC_DEF_ID_ = 'myProcess_Part1:1:f3cbd21f-2a0b-11eb-99cb-a6ee3527a6d6', DESCRIPTION_ = NULL, DUE_DATE_ = NULL, CATEGORY_ = NULL, SUSPENSION_STATE_ = 1, FORM_KEY_ = NULL, CLAIM_TIME_ = NULL where ID_= '86080ab6-2a0c-11eb-b436-a6ee3527a6d6' and REV_ = 2
         *
         */
        runtimeService.activateProcessInstanceById("86039de2-2a0c-11eb-b436-a6ee3527a6d6");
        log.info("激活流程实例");
    }

    /**
     * 删除流程实例，重复删除或删除不存在的实例会报错
     *
     * 删除表 ACT_RU_EXECUTION 中 数据
     */
    @Test
    public void delProcessInstance(){

        /**
         * deleteProcessInstance参数：
         * 第一个：流程ID
         * 第二个：删除理由
         */
        runtimeService.deleteProcessInstance("fcb15608-2998-11eb-8371-7226adc5313f","reason");
        log.info("删除流程实例");
    }
}
