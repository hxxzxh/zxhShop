package com.zxhshop.vo;

import com.zxhshop.pojo.TbGoods;
import com.zxhshop.pojo.TbGoodsDesc;
import com.zxhshop.pojo.TbItem;
import com.zxhshop.pojo.TbItemCat;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {
    private TbGoods goods;
    private TbGoodsDesc goodsDesc;
    private List<TbItem> itemList;

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
