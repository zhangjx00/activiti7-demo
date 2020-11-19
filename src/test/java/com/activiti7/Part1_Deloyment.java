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
     */
    @Test
    public void initDeploymentBPMN() {


        /**
         * 以下是数据执行步骤：
         *
         * 第一步：
         * SELECT * FROM ACT_RE_PROCDEF
         * WHERE KEY_ = 'myProcess_Part1'
         * AND ( TENANT_ID_ = '' OR TENANT_ID_ IS NULL )
         * AND VERSION_ = (SELECT max( VERSION_ ) FROM ACT_RE_PROCDEF WHERE KEY_ = 'myProcess_Part1' AND ( TENANT_ID_ = '' OR TENANT_ID_ IS NULL ))
         *
         * 第二步：
         * select J.* from ACT_RU_TIMER_JOB J
         * inner join ACT_RE_PROCDEF P on J.PROC_DEF_ID_ = P.ID_
         * where J.HANDLER_TYPE_ = ? and P.KEY_ = ? and (P.TENANT_ID_ = '' or P.TENANT_ID_ is null)
         *
         * 第三步：
         * select * from ACT_PROCDEF_INFO where PROC_DEF_ID_ = ?
         *
         * 第四步：
         * insert into ACT_RE_PROCDEF(ID_, REV_, CATEGORY_, NAME_, KEY_, VERSION_, DEPLOYMENT_ID_, RESOURCE_NAME_, DGRM_RESOURCE_NAME_, DESCRIPTION_, HAS_START_FORM_KEY_, HAS_GRAPHICAL_NOTATION_ , SUSPENSION_STATE_, TENANT_ID_, ENGINE_VERSION_)
         * values (?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
         *
         * 第五步：
         * insert into ACT_RE_DEPLOYMENT(ID_, NAME_, CATEGORY_, KEY_, TENANT_ID_, DEPLOY_TIME_, ENGINE_VERSION_, VERSION_, PROJECT_RELEASE_VERSION_)
         * values(?, ?, ?, ?, ?, ?, ?, ?, ?)
         *
         * 第六步：
         * insert into ACT_GE_BYTEARRAY(ID_, REV_, NAME_, BYTES_, DEPLOYMENT_ID_, GENERATED_) values (?, 1, ?, ?, ?, ?)
         *
         */
        String filename = "BPMN/Part1_Deployment.bpmn";
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(filename)
                .name("流程部署测试候选人task")
                .key("part1-3")
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
     * 获取流程列表
     */
    @Test
    public void getDeployments() {

        /**
         * select distinct RES.* from ACT_RE_DEPLOYMENT RES order by RES.ID_ asc LIMIT ? OFFSET ?
         */
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for(Deployment dep : list){
            System.out.println("Id："+dep.getId());
            System.out.println("Name："+dep.getName());
            System.out.println("DeploymentTime："+dep.getDeploymentTime());
            System.out.println("Key："+dep.getKey());
        }

    }
}
