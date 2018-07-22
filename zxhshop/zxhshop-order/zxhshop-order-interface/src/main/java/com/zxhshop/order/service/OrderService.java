package com.zxhshop.order.service;

import com.zxhshop.pojo.TbOrder;
import com.zxhshop.pojo.TbPayLog;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 将购物车列表的信息保存成订单基本，明细信息和支付日志信息
     * @param order 订单基本信息
     * @return 支付业务id
     */
    String addOrder(TbOrder order);

    /**
     * 根据日志id查询日志信息
     * @param outTradeNo 日志id
     * @return 日志信息
     */
    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    /**
     * 修改订单和支付日志的状态
     * @param outTradeNo 日志id
     * @param transactionId 微信对应的支付id
     */
    void updateOrderStatus(String outTradeNo, String transactionId);


}