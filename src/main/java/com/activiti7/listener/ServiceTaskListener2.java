package com.activiti7.listener;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

@Slf4j
public class ServiceTaskListener2 implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

        execution.getVariable("aa");
System.out.println(execution.getVariable("aa"));

    }
}
