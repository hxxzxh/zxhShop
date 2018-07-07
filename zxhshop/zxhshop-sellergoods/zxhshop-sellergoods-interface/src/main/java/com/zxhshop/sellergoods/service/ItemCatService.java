package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbItemCat;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface ItemCatService extends BaseService<TbItemCat> {

    PageResult search(Integer page, Integer rows, TbItemCat itemCat);

    /**
     * 将商品分类数据更新到redis中
     * 以分类名称为field，模板id为值
     */
    void updateItemCatToRedis();
}