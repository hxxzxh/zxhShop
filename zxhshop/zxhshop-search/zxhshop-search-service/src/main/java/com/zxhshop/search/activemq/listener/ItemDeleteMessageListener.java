package com.zxhshop.search.activemq.listener;

import com.zxhshop.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.*;
import java.io.Serializable;
import java.util.Arrays;

public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        itemSearchService.deleteItemByGoodsIdList(Arrays.asList(goodsIds));
        System.out.println("同步删除索引库中数据完成。");
    }
}
