package com.activiti7;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Part2_ProcessDefinition {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 查询流程定义
     * 从 ACT_RE_PROCDEF 表中获取数据
     */
    @Test
    public void getDefinitions(){

        /**
         * select distinct RES.* from ACT_RE_PROCDEF RES order by RES.ID_ asc LIMIT 2147483647 OFFSET 0
         */
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for(ProcessDefinition pd : list){
            System.out.println("------流程定义--------");
            System.out.println("Name："+pd.getName());
            System.out.println("Key："+pd.getKey());
            System.out.println("ResourceName："+pd.getResourceName());
            System.out.println("DeploymentId："+pd.getDeploymentId());
            System.out.println("Version："+pd.getVersion());

        }

    }

    /**
     * 删除流程定义
     */
    @Test
    public void delDefinition(){

        String pdID="34534758-2a08-11eb-926e-a6ee3527a6d6";

        /**
         * 删除之前先判断流程是否存在，不存在则报错。
         *
         * deleteDeployment 第二个参数设置true，删除流程实例及历史数据：
         * 1、delete from ACT_GE_BYTEARRAY where DEPLOYMENT_ID_ = ?
         * 2、delete from ACT_RE_DEPLOYMENT where ID_ = ?
         * 3、delete from ACT_RU_EVENT_SUBSCR where PROC_DEF_ID_ = ? and EXECUTION_ID_ is null and PROC_INST_ID_ is null
         * 4、delete from ACT_RU_IDENTITYLINK where PROC_DEF_ID_ = ?
         * 5、delete from ACT_RE_PROCDEF where DEPLOYMENT_ID_ = ?
         *
         * deleteDeployment 第二个参数设置false，删除流程实例，不删除历史数据：
         * 1、delete from ACT_GE_BYTEARRAY where DEPLOYMENT_ID_ = ?
         * 2、delete from ACT_RE_DEPLOYMENT where ID_ = ?
         * 3、delete from ACT_RE_PROCDEF where DEPLOYMENT_ID_ = ?
         *
         */
        repositoryService.deleteDeployment(pdID,false);
        System.out.println("删除流程定义成功");
    }
}
