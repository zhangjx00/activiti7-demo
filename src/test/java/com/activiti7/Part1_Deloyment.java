package com.activiti7;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 流程部署
 *
 * @author: zhangjx
 */
@SpringBootTest
public class Part1_Deloyment {

    @Autowired
    private RepositoryService repositoryService;


    /**
     * 通过bpmn部署流程
     * 保存到表：
     * ACT_GE_BYTEARRAY 流程定义二进制表
     * ACT_RE_DEPLOYMENT    部署信息表
     * ACT_RE_PROCDEF   流程定义数据表
     */
    @Test
    public void initDeploymentBPMN() {

        String filename = "BPMN/Part1_Deployment.bpmn";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                .name("流程部署测试候选人task")
                .deploy();
        System.out.println(deployment.getName());
    }

    /**
     * 部署流程，也可以部署图片（版本7可以通过插件生成图片，7版本下不用了）
     * ACT_GE_BYTEARRAY 产生2条数据
     */
    @Test
    public void initDeploymentPic(){
        //如果流程名字相同，会在名字后加_a
        String filename="BPMN/Part1_Deployment.bpmn";
        String pngname="BPMN/Part1_Deployment.png";
        Deployment deployment=repositoryService.createDeployment()
                .addClasspathResource(filename)
                .addClasspathResource(pngname)
                .name("流程部署测试候选人task-图片")
                .deploy();
        System.out.println(deployment.getName());
    }


    /**
     * 通过ZIP部署流程
     */
    @Test
    public void initDeploymentZIP() {
        InputStream fileInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("BPMN/Part1_DeploymentV2.zip");
        ZipInputStream zip=new ZipInputStream(fileInputStream);
        Deployment deployment=repositoryService.createDeployment()
                .addZipInputStream(zip)
                .name("流程部署测试zip")
                .deploy();
        System.out.println(deployment.getName());
    }


    /**
     * 查询流程部署
     */
    @Test
    public void getDeployments() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for(Deployment dep : list){
            System.out.println("Id："+dep.getId());
            System.out.println("Name："+dep.getName());
            System.out.println("DeploymentTime："+dep.getDeploymentTime());
            System.out.println("Key："+dep.getKey());
        }

    }
}
