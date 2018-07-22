package com.zxhshop.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhshop.common.util.IdWorker;
import com.zxhshop.common.util.RedisLock;
import com.zxhshop.mapper.SeckillGoodsMapper;
import com.zxhshop.mapper.SeckillOrderMapper;
import com.zxhshop.pojo.TbSeckillGoods;
import com.zxhshop.pojo.TbSeckillOrder;
import com.zxhshop.seckill.service.SeckillOrderService;
import com.zxhshop.service.impl.BaseServiceImpl;
import com.zxhshop.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillOrderService.class)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {


    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    private static final String SECKILL_GOODS = "SECKILL_GOODS";
    private static final String SECKILL_ORDERS = "SECKILL_ORDERS";


    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillOrder.get***())){
            criteria.andLike("***", "%" + seckillOrder.get***() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public Long submitOrder(String username, Long seckillId) throws Exception {
        RedisLock redisLock = new RedisLock(redisTemplate);
        if (redisLock.lock(seckillId.toString())) {
            //根据商品id查询秒杀商品信息
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(seckillId);
            //如果商品不存在抛异常
            if (seckillGoods == null) {
                throw new RuntimeException("商品不存在");
            }
            //商品库存是否为0
            if (seckillGoods.getNum() == 0) {
                throw new RuntimeException("商品已抢完了");
            }

            //扣除商品数量
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
            if (seckillGoods.getStockCount() > 0) {
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillId, seckillGoods);
            } else {
                //库存为0，则删除redis中的数据
                redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillId);
                //记录到数据库中
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            }

            redisLock.unlock(seckillId.toString());
            //生成订单保存在redis中
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            Long orderId = idWorker.nextId();
            seckillOrder.setId(orderId);
            seckillOrder.setUserId(username);
            seckillOrder.setStatus("0");//未支付
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setSeckillId(seckillId);
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            seckillOrder.setMoney(seckillGoods.getCostPrice());

            redisTemplate.boundHashOps(SECKILL_ORDERS).put(orderId.toString(), seckillOrder);
            return orderId;
        }

        return null;


    }

    @Override
    public TbSeckillOrder getSeckillOrderInRedisByOderId(String orderId) {

        return (TbSeckillOrder) redisTemplate.boundHashOps(SECKILL_ORDERS).get(orderId);
    }

    @Override
    public void saveOrderInRedisToDb(String orderId, String transactionId) {
        TbSeckillOrder seckillOrder = getSeckillOrderInRedisByOderId(orderId);

        if (seckillOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!orderId.equals(seckillOrder.getId().toString())) {
            throw new RuntimeException("订单不相符");
        }

        seckillOrder.setStatus("1");//已支付
        seckillOrder.setTransactionId(transactionId);
        seckillOrder.setPayTime(new Date());
        //保存订单到数据库中
        seckillOrderMapper.insertSelective(seckillOrder);
        //删除redis中的订单数据
        redisTemplate.boundHashOps(SECKILL_ORDERS).delete(orderId);
    }

    @Override
    public void deleteOrderInRedis(String orderId) throws InterruptedException {
        TbSeckillOrder seckillOrder = getSeckillOrderInRedisByOderId(orderId);
        if (seckillOrder != null && orderId.equals(seckillOrder.getId().toString())) {
            //1.删除redis中对应的订单数据
            redisTemplate.boundHashOps(SECKILL_ORDERS).delete(orderId);
            //2.更新订单对应商品的库存数据
            RedisLock redisLock = new RedisLock(redisTemplate);
            if (redisLock.lock(seckillOrder.getSeckillId().toString())) {//加分布式锁
                TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(seckillOrder.getSeckillId());
                if (seckillGoods == null) {
                    seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                }
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);

                //释放分布式锁
                redisLock.unlock(seckillOrder.getSeckillId().toString());

            }
        }
    }
}
