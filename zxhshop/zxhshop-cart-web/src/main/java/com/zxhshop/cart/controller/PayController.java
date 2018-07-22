package com.zxhshop.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.order.service.OrderService;
import com.zxhshop.pay.service.WeixinPayService;
import com.zxhshop.pojo.TbPayLog;
import com.zxhshop.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {
    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    /**
     *  根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     * @param outTradeNo 支付订单
     * @return 二维码地址等信息
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        //查找支付日志信息
        TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);
        if (payLog != null) {
            return weixinPayService.createNative(outTradeNo, payLog.getTotalFee().toString());
        }
        return new HashMap<>();
    }

    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result = Result.fail("支付失败");
        try {
            int count = 0;
            while (true) {
                //到微信支付查询支付状态
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                if (resultMap == null) {
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    result = Result.ok("支付成功");
                    //更新订单、日志状态
                    orderService.updateOrderStatus(outTradeNo, resultMap.get("transaction_id"));
                    break;
                }

                count++;

                if (count > 60) {
                    result = Result.fail("支付超时");
                    break;
                }
                //每三秒休眠一次
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
