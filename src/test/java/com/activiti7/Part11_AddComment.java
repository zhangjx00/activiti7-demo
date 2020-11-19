package com.activiti7;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @SpringBootTest
 * @author: zhangjx
 */
@SpringBootTest
public class Part11_AddComment {

    private static final String TASK_ID = "";
    private static final String PROCESS_INSTANCE_ID = "";
    private static final String USER_ID = "bajje";


    @Autowired
    protected TaskService taskService;
    @Autowired
    protected HistoryService historyService;

    @Test
    public void check() {

        /**
         * 1、部署bpmn
         * 2、启动流程. Part3_ProcessInstance.initProcessInstance()
         * 3、bajie审批同意.
         * 4、悟空审批同意
         */

        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(USER_ID)
                .list();
        for(Task tk : list){
            //增加comment
            taskService.addComment(tk.getId(), tk.getProcessInstanceId(), "common2022");
            taskService.complete(tk.getId());
        }

    }

    @Test
    public void getComment() {

        List<Task> list = taskService.createTaskQuery()
                .taskAssignee(USER_ID)
                .list();
        for(Task tk : list){
            //增加comment
            List<Comment> taskComments = taskService.getTaskComments(tk.getId());
            System.out.println("taskComments");
        }

        // taskService.complete(taskId);
    }

}
