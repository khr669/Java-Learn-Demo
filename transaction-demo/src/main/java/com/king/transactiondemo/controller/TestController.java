package com.king.transactiondemo.controller;

import com.king.transactiondemo.service.UserRepository;
import com.king.transactiondemo.service.impl.CustomCheckedException;
import com.king.transactiondemo.service.impl.TransactionDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TransactionDemoService demoService;

    // 清空数据库，方便每次实验
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/clear")
    public String clear() {
        userRepository.deleteAll();
        return "Database cleared.";
    }

    // 触发实验2 - 失效场景
    @GetMapping("/s2/fail")
    public String s2fail() {
        try {
            demoService.scenario2_checkedExceptionFails();
        } catch (CustomCheckedException e) {
            return "Caught checked exception. Check DB.";
        }
        return "Should not reach here.";
    }

    // 触发实验2 - 修复场景
    @GetMapping("/s2/fix")
    public String s2fix() {
        try {
            demoService.scenario2_fix();
        } catch (CustomCheckedException e) {
            return "Caught checked exception with rollbackFor. Check DB.";
        }
        return "Should not reach here.";
    }

    // 触发实验3
    @GetMapping("/s3")
    public String s3() {
        demoService.scenario3_internalCallFails();
        return "Internal call executed. Check DB.";
    }

    // 触发实验6
    @GetMapping("/s6")
    public String s6() {
        demoService.scenario6_privateMethodFails();
        return "Private method call executed. Check DB.";
    }

    // 触发嵌套事务
    @GetMapping("/nested")
    public String nested() {
        demoService.propagation_testNested();
        return "Nested transaction test executed. Check DB.";
    }
}
