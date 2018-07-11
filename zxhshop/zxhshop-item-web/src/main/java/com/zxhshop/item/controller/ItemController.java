package com.zxhshop.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pojo.TbItemCat;
import com.zxhshop.sellergoods.service.GoodsService;
import com.zxhshop.sellergoods.service.ItemCatService;
import com.zxhshop.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    /**
     * 跳转商品详情页面显示商品
     * @param goodsId 商品id
     * @return 商品详情页
     */
    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId) {
        ModelAndView mv = new ModelAndView("item");

        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

        //商品基本信息
        mv.addObject("goods", goods.getGoods());
        //商品描述信息
        mv.addObject("goodsDesc", goods.getGoodsDesc());

        TbItemCat itemcat = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1", itemcat.getName());

        itemcat = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2", itemcat.getName());

        itemcat = itemCatService.findOne(goods.getGoods().getCategory3Id());
        mv.addObject("itemCat3", itemcat.getName());

        //商品SKU列表
        mv.addObject("itemList", goods.getItemList());

        return mv;
    }
}
