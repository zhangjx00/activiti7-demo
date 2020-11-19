package com.activiti7;


import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class Part6_UEL {


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    /**
     * 启动流程实例带参数，执行执行人
     *
     * 流程图描述：审批人用uel表达式 ${ZhiXingRen}
     */
    @Test
    public void initProcessInstanceWithArgs() {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("ZhiXingRen", "wukong");

        //流程变量，变量可以多个
        //variables.put("ZhiXingRen2", "aaa");
        //variables.put("ZhiXingRen3", "wukbbbong");

        /**
         * startProcessInstanceByKey第三个参数是变量
         */
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        "myProcess_UEL_V1"
                        , "bKey002"
                        , variables);
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());

    }


    /**
     * 完成任务带参数，指定流程变量测试
     */
    @Test
    public void completeTaskWithArgs() {
        Map<String, Object> variables = new HashMap<String, Object>();
        // //流程图描述：审批人用uel表达式 ${ZhiXingRen}
        // variables.put("ZhiXingRen", "wukong");

        //流程图描述：执行人是八戒，增加一个网管，如果报销大于100悟空审核，否则唐僧审核，条件用uel表达式 ${pay}
        variables.put("pay", "101");
        taskService.complete("a616ea19-d3a7-11ea-9e14-dcfb4875e032",variables);
        System.out.println("完成任务");
    }


    /**
     * 启动流程实例带参数，使用实体类
     */
    @Test
    public void initProcessInstanceWithClassArgs() {
        UEL_POJO uel_pojo = new UEL_POJO();
        uel_pojo.setZhixingren("bajie");

        //流程变量
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("uelpojo", uel_pojo);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        "myProcess_uelv3"
                        , "bKey002"
                        , variables);
        System.out.println("流程实例ID：" + processInstance.getProcessDefinitionId());

    }

    /**
     * 任务完成环节带参数，指定多个候选人
     */
    @Test
    public void initProcessInstanceWithCandiDateArgs() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("houxuanren", "wukong,tangseng");
        taskService.complete("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032",variables);
        System.out.println("完成任务");
    }

    /**
     * 直接指定流程变量
     */
    @Test
    public void otherArgs() {
        runtimeService.setVariable("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032","pay","101");
        //设置多个变量
//        runtimeService.setVariables();

        //任务执行阶段设置变量
//        taskService.setVariable();
//        taskService.setVariables();

    }


    /**
     * 局部变量。只有本方法内部有效
     */
    @Test
    public void otherLocalArgs() {
        runtimeService.setVariableLocal("4f6c9e23-d3ae-11ea-82ba-dcfb4875e032","pay","101");
//        runtimeService.setVariablesLocal();
//        taskService.setVariableLocal();
//        taskService.setVariablesLocal();
    }

}
