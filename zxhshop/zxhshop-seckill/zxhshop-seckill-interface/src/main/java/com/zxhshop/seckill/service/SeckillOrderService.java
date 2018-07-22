package com.zxhshop.seckill.service;

import com.zxhshop.pojo.TbSeckillOrder;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    /**
     * 提交订单保存到redis
     * @param username 用户id
     * @param seckillId 秒杀商品id
     * @return 秒杀订单id
     */
    Long submitOrder(String username, Long seckillId) throws Exception;

    /**
     * 根据订单id在redis数据库中查询秒杀订单信息
     * @param orderId 订单id
     * @return 秒杀订单信息
     */
    TbSeckillOrder getSeckillOrderInRedisByOderId(String orderId);

    /**
     * 更新在redis中的订单支付状态并保存到数据库中
     * @param orderId 订单id
     * @param transactionId 微信支付id
     */
    void saveOrderInRedisToDb(String orderId, String transactionId);

    /**
     * 删除redis中的订单数据
     * 删除订单后需要恢复对应秒杀商品的库存
     * @param orderId 订单id
     */
    void deleteOrderInRedis(String orderId) throws Exception;

}