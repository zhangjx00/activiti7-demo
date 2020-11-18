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
     * 从ACT_RE_PROCDEF表中获取数据
     */
    @Test
    public void getDefinitions(){
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

        String pdID="af5e803b-295f-11eb-9d48-ca4d2811b930";
        /**
         * deleteDeployment第二个参数如果是true，删除流程实例，记录及历史；如果选择false则会保留历史
         */
        repositoryService.deleteDeployment(pdID,true);
        System.out.println("删除流程定义成功");
    }
}
