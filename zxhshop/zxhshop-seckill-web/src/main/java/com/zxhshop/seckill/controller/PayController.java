package com.zxhshop.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zxhshop.pay.service.WeixinPayService;
import com.zxhshop.pojo.TbSeckillOrder;
import com.zxhshop.seckill.service.SeckillOrderService;
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
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeixinPayService weixinPayService;

    /**
     *  根据秒杀订单 id 到微信支付创建支付订单并返回支付二维码地址等信息
     * @param outTradeNo 支付订单
     * @return 二维码地址等信息
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo) {
        //查找秒杀订单信息
        TbSeckillOrder seckillOrder = seckillOrderService.getSeckillOrderInRedisByOderId(outTradeNo);
        if (seckillOrder != null) {
            return weixinPayService.createNative(outTradeNo, ((long)(seckillOrder.getMoney().doubleValue()*100))+"");
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
                    //更新订单支付状态
                    seckillOrderService.saveOrderInRedisToDb(outTradeNo, resultMap.get("transaction_id"));
                    break;
                }

                count++;

                if (count > 5) {
                    result = Result.fail("支付超时");
                    //关闭微信支付的订单
                    resultMap = weixinPayService.closeOrder(outTradeNo);
                    if ("ORDERPAID".equals(resultMap.get("err_code"))){//如果关闭订单中被支付了，那么标识为支付成功
                        result = Result.ok("支付成功");
                        //需要更新订单 状态
                        seckillOrderService.saveOrderInRedisToDb(outTradeNo,resultMap.get("transaction_id"));
                        break;
                    }
                    //删除redis中的订单数据
                    seckillOrderService.deleteOrderInRedis(outTradeNo);
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
