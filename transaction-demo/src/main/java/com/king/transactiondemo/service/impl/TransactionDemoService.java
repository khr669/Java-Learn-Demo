package com.king.transactiondemo.service.impl;

import com.king.transactiondemo.entity.User;
import com.king.transactiondemo.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionDemoService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private TransactionDemoService self; // 注入自己的代理，用于后续实验

    // --- 实验2：默认配置下，受检异常不会导致回滚 ---
    @Transactional
    public void scenario2_checkedExceptionFails() throws CustomCheckedException {
        userRepository.save(new User("user-for-checked-exception"));
        throw new CustomCheckedException("This is a checked exception.");
    }

    // --- 实验2的修复方案：指定rollbackFor ---
    @Transactional(rollbackFor = CustomCheckedException.class)
    public void scenario2_fix() throws CustomCheckedException {
        userRepository.save(new User("user-for-checked-exception-fix"));
        throw new CustomCheckedException("This is a checked exception with rollbackFor.");
    }

    // --- 实验3：this调用导致事务失效 ---
    public void scenario3_internalCallFails() {
        // 这个外部方法没有事务注解
        // 通过this调用，将导致@Transactional被忽略
        this.internalTransactionalMethod();
    }

    @Transactional
    public void internalTransactionalMethod() {
        userRepository.save(new User("user-from-internal-call"));
    }


    // --- 实验6：非public方法导致事务失效 ---
    public void scenario6_privateMethodFails() {
        // 这个public方法调用一个private的事务方法
        try {
            this.privateTransactionalMethod();
        } catch (RuntimeException e) {
            System.out.println("捕获到异常: " + e.getMessage());
        }
    }

    @Transactional
    protected void privateTransactionalMethod() {
        userRepository.save(new User("user-from-private-method"));
        throw new RuntimeException("Error in private method.");
    }

    // --- 实验：嵌套事务 (NESTED) ---
    @Transactional
    public void propagation_testNested() {
        userRepository.save(new User("Parent User"));
        try {
            // 必须通过代理self调用，才能让传播行为生效
            self.nestedChildMethod();
        } catch (RuntimeException e) {
            System.out.println("父事务捕获到子事务异常，但不影响父事务提交");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public void nestedChildMethod() {
        userRepository.save(new User("Child User"));
        throw new RuntimeException("Nested transaction failed.");
    }
}