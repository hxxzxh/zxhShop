package com.zxhshop.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhshop.mapper.UserMapper;
import com.zxhshop.pojo.TbUser;
import com.zxhshop.user.service.UserService;
import com.zxhshop.service.impl.BaseServiceImpl;
import com.zxhshop.vo.PageResult;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl extends BaseServiceImpl<TbUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue zxhSmsQueue;

    @Value("${signName}")
    private String signName;

    @Value("${templateCode}")
    private String templateCode;

    @Override
    public PageResult search(Integer page, Integer rows, TbUser user) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(user.get***())){
            criteria.andLike("***", "%" + user.get***() + "%");
        }*/

        List<TbUser> list = userMapper.selectByExample(example);
        PageInfo<TbUser> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void sendSmsCode(String phone) {
        //1.生成随机的6位数验证码
        String code = (long)(Math.random() * 1000000) + "";
        System.out.println("验证码为：" + code);

        //2.存入redis中，并设置保存5分钟
        redisTemplate.boundValueOps(phone).set(code);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);

        //3.发送短信相关参数到activeMQ
        jmsTemplate.send(zxhSmsQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);
                mapMessage.setString("signName", signName);
                mapMessage.setString("templateCode", templateCode);
                mapMessage.setString("templateParam","{\"code\":"+code+"}");
                return mapMessage;
            }
        });


    }

    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        String code = (String) redisTemplate.boundValueOps(phone).get();
        if (code.equals(smsCode)) {
            redisTemplate.delete(phone);
            return true;
        }
        return false;
    }
}
