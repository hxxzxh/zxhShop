package com.zxhshop.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.zxhshop.common.util.HttpClient;
import com.zxhshop.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;


import java.util.HashMap;
import java.util.Map;

@Service(interfaceClass = WeixinPayService.class)
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String mch_id;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notify_url;


    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {
        Map<String, String> returnMap = new HashMap<>();

        try {
            //1.组合要发送到微信支付的参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", appid);// 从微信申请的公众账号 ID
            paramMap.put("mch_id", mch_id);//微信支付分配的商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //paramMap.put("sign","");// 微信 sdk 提供有工具类包生成
            paramMap.put("body", "zxhshop");//商品简单描述
            paramMap.put("out_trade_no", outTradeNo);//商户系统内部订单号
            paramMap.put("total_fee", totalFee);//订单总金额，单位为分
            paramMap.put("spbill_create_ip", "127.0.0.1");//当前机器 ip
            paramMap.put("notify_url", notify_url);//回调地址
            paramMap.put("trade_type", "NATIVE ");//交易类型:扫码支付
            //2.将map转换成我微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送到微信支付统一下单的XML为：" + signedXml);

            //3.创建httpClient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //4.微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println("微信支付统一下单返回的数据为：" + content);

            //5.转换内容为XML并返回设置结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            returnMap.put("result_code", resultMap.get("result_code"));//业务结果
            returnMap.put("code_url", resultMap.get("code_url"));//二维码支付地址
            returnMap.put("outTradeNo", outTradeNo);//订单号
            returnMap.put("totalFee", totalFee);//总金额

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
            //组合要发送的参数
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("appid", appid);// 从微信申请的公众账号 ID
            paramMap.put("mch_id", mch_id);//微信支付分配的商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //paramMap.put("sign","");// 微信 sdk 提供有工具类包生成
            paramMap.put("out_trade_no", outTradeNo);

            //2.将map转换成我微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送到微信支付查询订单状态的XML为：" + signedXml);

            //3.创建httpClient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            //4.获取查看订单返回的数据
            String content = httpClient.getContent();
            System.out.println("微信支付查看订单状态返回的数据为：" + content);

            //5.转换内容为XML并返回设置结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
