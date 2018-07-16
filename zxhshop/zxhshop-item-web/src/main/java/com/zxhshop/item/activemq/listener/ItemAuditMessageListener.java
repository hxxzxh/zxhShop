package com.zxhshop.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pojo.TbItemCat;
import com.zxhshop.sellergoods.service.GoodsService;
import com.zxhshop.sellergoods.service.ItemCatService;
import com.zxhshop.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ItemAuditMessageListener extends AbstractAdaptableMessageListener{
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();
        for (Long goodsId : goodsIds) {
            getItemHtml(goodsId);
        }
        System.out.println("同步生成商品静态化页面完成");
    }

    private void getItemHtml(Long goodsId) {
        try {
            //获取模板
            Configuration configuration = freeMarkerConfigurer.getConfiguration();

            //configuration.setDefaultEncoding("utf-8");

            Template template = configuration.getTemplate("item.ftl");

            Map<String, Object> dataModel = new HashMap<>();

            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            dataModel.put("goods", goods.getGoods());
            dataModel.put("goodsDesc", goods.getGoodsDesc());

            TbItemCat itemcat = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemcat.getName());

            itemcat = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemcat.getName());

            itemcat = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemcat.getName());

            dataModel.put("itemList", goods.getItemList());

            //输出到指定路径
            String filename = ITEM_HTML_PATH + goodsId + ".html";
//            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
            template.process(dataModel, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
