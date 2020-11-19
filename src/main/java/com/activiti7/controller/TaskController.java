package com.activiti7.controller;

import com.activiti7.util.SecurityUtil;
import com.activiti7.mapper.ActivitiMapper;
import com.activiti7.util.AjaxResponse;
import com.activiti7.util.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/task")
@Slf4j
public class TaskController {
    @Autowired
    private TaskRuntime taskRuntime;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ProcessRuntime processRuntime;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    ActivitiMapper mapper;

    /**
     * 获取我的待办任务
     *
     * @return
     */
    @GetMapping(value = "/getTasks")
    public AjaxResponse getTasks() {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("wukong");
            }

            /**
             * tasks方法获取当前登录人待办数据，如果使用别的权限框架，这个方法不能使用。
             * tasks没有流程实例名称
             */
            Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 100));

            List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
            for (Task tk : tasks.getContent()) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", tk.getId());
                hashMap.put("name", tk.getName());
                hashMap.put("status", tk.getStatus());
                hashMap.put("createdDate", tk.getCreatedDate());

                /**
                 * tasks方法会查出执行人和候选人都是当前用户的数据
                 *
                 * getAssignee()是null时，当前用户是候选人，前台显示未拾取。否则当前用户是执行人
                 */
                if (tk.getAssignee() == null) {
                    hashMap.put("assignee", "待拾取任务");
                } else {
                    hashMap.put("assignee", tk.getAssignee());//
                }

                //tk没有流程实例名称，通过processInstance获取
                ProcessInstance processInstance = processRuntime.processInstance(tk.getProcessInstanceId());
                hashMap.put("instanceName", processInstance.getName());

                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);

        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "获取我的代办任务失败", e.toString());
        }
    }

    /**
     * 完成待办任务
     *
     * @return
     */
    @GetMapping(value = "/completeTask")
    public AjaxResponse completeTask(@RequestParam("taskID") String taskID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("bajie");
            }

            Task task = taskRuntime.task(taskID);

            //如果当前用户是候选人，先拾取这个任务
            if (task.getAssignee() == null) {
                taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(task.getId()).build());
            }
            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(task.getId())
                    //执行环节可以设置变量
                    //.withVariable("num", "2")
                    .build());

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), null);

        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "完成失败", e.toString());
        }
    }

    /**
     * 启动
     *
     * @param processDefinitionKey
     * @param instanceName
     * @param instanceVariable
     * @return
     */
    @GetMapping(value = "/startProcess4")
    public AjaxResponse startProcess3(@RequestParam("processDefinitionKey") String processDefinitionKey,
                                      @RequestParam("instanceName") String instanceName,
                                      @RequestParam("instanceVariable") String instanceVariable) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("wukong");
            }


/*            @RequestMapping("/approval_msg")
            @ResponseBody
            public JsonResponse approvalPass(String id,String msg){
                JsonResponse jsonResponse = new JsonResponse();

                if(StringUtil.isNotEmpty(msg)){
                    String str= msg.replace("\"", "");
                    taskService.setVariable(id,"msg",str);
                }
                taskService.complete(id);
                return jsonResponse;
            }*/

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), null);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "失败", e.toString());
        }
    }


    /**
     * 渲染动态表单
     *
     * @param taskID
     * @return
     */
    @GetMapping(value = "/formDataShow")
    public AjaxResponse formDataShow(@RequestParam("taskID") String taskID) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("bajie");
            }
            //获取任务
            Task task = taskRuntime.task(taskID);

            /**
             * 获取表单控件历史数据
             *
             * 表 formdata 保存了所有的表单数据
             */
            //本实例所有保存的表单数据HashMap，为了快速读取控件以前环节存储的值
            HashMap<String, String> controlistMap = new HashMap<>();
            List<HashMap<String, Object>> tempControlList = mapper.selectFormData(task.getProcessInstanceId());
            for (HashMap ls : tempControlList) {
                controlistMap.put(ls.get("Control_ID_").toString(), ls.get("Control_VALUE_").toString());
            }


            /**
                ID格式：FormProperty_0ueitp2-_!类型-_!名称-_!默认值-_!是否参数。第一部分是流程自动生成的。

                例子：
                FormProperty_0lovri0-_!string-_!姓名-_!请输入姓名-_!f
                FormProperty_1iu6onu-_!int-_!年龄-_!请输入年龄-_!s

                默认值：无、字符常量、FormProperty_开头定义过的控件ID
                是否参数：f为不是参数，s是字符，t是时间(不需要int，因为这里int等价于string)
                注：类型是可以获取到的，但是为了统一配置原则，都配置到
            */

            /**
             * 注意!!!
             * 表单Key必须要任务编号一模一样，因为参数需要任务key，但是无法获取，只能获取表单key“task.getFormKey()”当做任务key
             */
            UserTask userTask = (UserTask) repositoryService.getBpmnModel(task.getProcessDefinitionId())
                    .getFlowElement(task.getFormKey());

            if (userTask == null) {
                return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), "无表单");
            }

            //获取表单集合
            List<FormProperty> formProperties = userTask.getFormProperties();
            List<HashMap<String, Object>> listMap = new ArrayList<>();
            for (FormProperty fp : formProperties) {

                /**
                 * 处理字符串"FormProperty_0lovri0-_!string-_!姓名-_!请输入姓名-_!f"
                 * 返回json字符串到前端
                 */
                String[] splitFP = fp.getId().split("-_!");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", splitFP[0]);
                hashMap.put("controlType", splitFP[1]);
                hashMap.put("controlLable", splitFP[2]);

                //默认值如果是表单控件ID
                if (splitFP[3].startsWith("FormProperty_")) {
                    //控件ID存在
                    if (controlistMap.containsKey(splitFP[3])) {
                        hashMap.put("controlDefValue", controlistMap.get(splitFP[3]));
                    } else {
                        //控件ID不存在
                        hashMap.put("controlDefValue", "读取失败，检查" + splitFP[0] + "配置");
                    }
                } else {
                    //默认值如果不是表单控件ID则写入默认值
                    hashMap.put("controlDefValue", splitFP[3]);
                }

                hashMap.put("controlIsParam", splitFP[4]);
                listMap.add(hashMap);
            }

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);

        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "失败", e.toString());
        }
    }

    /**
     * 保存表单
     *
     * @param taskID
     * @param formData
     * @return
     */
    @PostMapping(value = "/formDataSave")
    public AjaxResponse formDataSave(@RequestParam("taskID") String taskID,
                                     @RequestParam("formData") String formData) {
        try {
            if (GlobalConfig.Test) {
                securityUtil.logInAs("bajie");
            }

            Task task = taskRuntime.task(taskID);

            /**
             * 控件之间用"!_!"分隔，控件内部属性用"-_!"分隔
             *
             * formData:控件id-_!控件值-_!是否参数!_!控件id-_!控件值-_!是否参数
             * FormProperty_0lovri0-_!不是参数-_!f!_!FormProperty_1iu6onu-_!数字参数-_!s
             */
            HashMap<String, Object> variables = new HashMap<String, Object>();
            Boolean hasVariables = false;//没有任何参数

            List<HashMap<String, Object>> listMap = new ArrayList<>();

            //前端传来的字符串，拆分成每个控件
            String[] formDataList = formData.split("!_!");//
            for (String controlItem : formDataList) {
                String[] formDataItem = controlItem.split("-_!");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("PROC_DEF_ID_", task.getProcessDefinitionId());
                hashMap.put("PROC_INST_ID_", task.getProcessInstanceId());
                hashMap.put("FORM_KEY_", task.getFormKey());
                hashMap.put("Control_ID_", formDataItem[0]);
                hashMap.put("Control_VALUE_", formDataItem[1]);
                listMap.add(hashMap);

                //构建参数集合
                switch (formDataItem[2]) {
                    case "f":
                        System.out.println("控件值不作为参数");
                        break;
                    case "s":
                        variables.put(formDataItem[0], formDataItem[1]);
                        hasVariables = true;
                        break;
                    case "t":
                        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        variables.put(formDataItem[0], timeFormat.parse(formDataItem[2]));
                        hasVariables = true;
                        break;
                    case "b":
                        variables.put(formDataItem[0], BooleanUtils.toBoolean(formDataItem[2]));
                        hasVariables = true;
                        break;
                    default:
                        System.out.println("控件参数类型配置错误：" + formDataItem[0] + "的参数类型不存在，" + formDataItem[2]);
                }
            }

            if (hasVariables) {
                //带参数完成任务
                taskRuntime.complete(
                        TaskPayloadBuilder
                                .complete()
                                .withTaskId(taskID)
                                //这儿的 variables 会覆盖之前传入的变量
                                .withVariables(variables)
                                .build());
            } else {
                taskRuntime.complete(
                        TaskPayloadBuilder
                                .complete()
                                .withTaskId(taskID)
                                .build()
                );
            }

            //写入数据库
            mapper.insertFormData(listMap);

            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getDesc(), listMap);
        } catch (Exception e) {
            return AjaxResponse.AjaxData(GlobalConfig.ResponseCode.ERROR.getCode(), "失败", e.toString());
        }
    }

}
