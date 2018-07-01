package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbItemCat;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface ItemCatService extends BaseService<TbItemCat> {

    PageResult search(Integer page, Integer rows, TbItemCat itemCat);
}