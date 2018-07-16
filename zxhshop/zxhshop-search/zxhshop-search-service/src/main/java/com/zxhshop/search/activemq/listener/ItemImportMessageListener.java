package com.zxhshop.search.activemq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxhshop.pojo.TbItem;
import com.zxhshop.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        TextMessage textMessage = (TextMessage) message;

        List<TbItem> itemList = JSONArray.parseArray(textMessage.getText(), TbItem.class);
        for (TbItem item : itemList) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
        itemSearchService.importItemList(itemList);
        System.out.println("同步索引库数据完成");
    }
}
