package com.zxhshop.order.service;

import com.zxhshop.pojo.TbOrderItem;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface OrderItemService extends BaseService<TbOrderItem> {

    PageResult search(Integer page, Integer rows, TbOrderItem orderItem);
}