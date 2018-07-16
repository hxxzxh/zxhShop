package com.zxhshop.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.sellergoods.service.GoodsService;
import com.zxhshop.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;
import java.io.Serializable;

public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        for (Long goodsId : goodsIds) {
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
