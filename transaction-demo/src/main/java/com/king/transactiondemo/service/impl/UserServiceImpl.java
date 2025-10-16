package com.king.transactiondemo.service.impl;

import com.king.transactiondemo.entity.User;
import com.king.transactiondemo.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    // 注入自己（的代理对象）
    @Autowired
    @Lazy // 使用 @Lazy 解决循环依赖问题
    private UserServiceImpl self;

    /**
     * 方法A: 作为外部调用的入口，有事务。
     * 它会调用内部的方法B来创建两个用户。
     * 在创建第二个用户后，它会抛出异常，目的是为了触发事务回滚。
     */
    @Transactional
    public void createUsersWrong() {
        // 调用方法B，尝试创建一个叫 "Alice" 的用户
        self.createUser("Alice"); // 问题就在这里！使用了 this 调用

        // 制造一个运行时异常，模拟业务出错,触发事务回滚
        throw new RuntimeException("Some error occurred!");
    }

    /**
     * 方法B: 内部方法，我们希望它能有独立的事务。
     * 使用 REQUIRES_NEW 意味着，每次调用它都应该开启一个全新的事务。
     * 如果外部事务回滚，不应该影响到这个新事务（如果它已经成功提交）。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createUser(String name) {
        userRepository.save(new User(name));
    }
}
