package com.zxhshop.task;

import com.zxhshop.mapper.SeckillGoodsMapper;
import com.zxhshop.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {
    private static final String SECKILL_GOODS = "SECKILL_GOODS";
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     *  每分钟执行查询秒杀商品数据库表，
     *  将审核通过的，库存大于 0 ，开始时间小于等于当前时间，
     *  结束时间大于当前时间并且缓存中不存在的秒杀商品存入缓存。
     */
    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoods() {
        List ids = new ArrayList(redisTemplate.boundHashOps(SECKILL_GOODS).keys()) ;

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "1");
        criteria.andGreaterThan("stockCount", 0);
        criteria.andLessThanOrEqualTo("startTime", new Date());
        criteria.andGreaterThan("endTime", new Date());
        if (ids.size()>0) {
            criteria.andNotIn("id", ids);
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        if (seckillGoodsList!=null&&seckillGoodsList.size()>0) {
            for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);
            }
            System.out.println("已将 " + seckillGoodsList.size() + " 条数据加入到redis中");
        }


    }

    /**
     *  每秒钟都去检查 redis 中的商品是否过期；
     *  若过期则从 redis 中移除秒杀商品并将该商品更新到数据库中。
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods() {
        //查询redis中的商品数据
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps(SECKILL_GOODS).values();

        if (seckillGoods != null && seckillGoods.size() > 0) {
            for (TbSeckillGoods seckillGood : seckillGoods) {
                //判断结束时间是否小于当前时间，若是则过期移除
                if (seckillGood.getEndTime().getTime() < new Date().getTime()) {
                    //保存到数据库
                    seckillGoodsMapper.updateByPrimaryKeySelective(seckillGood);
                    //从redis中删除
                    redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillGood.getId());
                    System.out.println("移除过期的秒杀商品：" + seckillGood.getId());
                }
            }
        }
    }
}
