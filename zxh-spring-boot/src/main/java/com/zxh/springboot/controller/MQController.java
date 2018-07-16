package com.zxh.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/mq")
@RestController
public class MQController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/send")
    public String sendMapMsg() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", 123L);
        map.put("name", "hhh");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue", map);

        return "发送消息完成";
    }

    @GetMapping("/sendSms")
    public String sendSms() {
        Map<String, String> map = new HashMap<>();
//        String mobile,String signName,String templateCode,String templateParam
        map.put("mobile", "13144066269");
        map.put("signName", "郑星煌");
        map.put("templateCode", "SMS_139234139");
        map.put("templateParam", "{\"code\":\"123456\"}");
        jmsMessagingTemplate.convertAndSend("zxh_sms_queue", map);
        return "发送sms消息完成";

    }
}
