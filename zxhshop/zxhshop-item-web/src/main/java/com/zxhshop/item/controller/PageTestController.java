package com.zxhshop.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pojo.TbItemCat;
import com.zxhshop.sellergoods.service.GoodsService;
import com.zxhshop.sellergoods.service.ItemCatService;
import com.zxhshop.vo.Goods;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class PageTestController {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 审核视频后生成商品html页面到指定路径
     *
     * @param goodsIds 商品id集合
     * @return
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds) {
        for (Long goodsId : goodsIds) {
            getItemHtml(goodsId);
        }
        return "success";
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

    @GetMapping("/delete")
    public String delete(Long[] goodsIds) {
        for (Long goodsId : goodsIds) {
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
        return "success";
    }
}
