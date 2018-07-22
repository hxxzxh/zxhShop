package com.zxhshop.pay.service;

import java.util.Map;

public interface WeixinPayService {
    /**
     * 根据支付日志id到微信支付创建支付订单并返回支付二维码地址等信息
     * @param outTradeNo 日志id
     * @param totalFee 支付总金额
     * @return 支付二维码地址等信息
     */
    Map<String,String> createNative(String outTradeNo, String totalFee);

    /**
     * 查询支付状态
     * @param outTradeNo 日志id
     * @return 支付状态
     */
    Map<String,String> queryPayStatus(String outTradeNo);

    /**
     *根据支付日志id关闭微信上的订单
     * @param outTradeNo 支付日志id（订单id）
     * @return 关闭结果
     */
    Map<String,String> closeOrder(String outTradeNo);
}
