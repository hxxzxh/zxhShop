package com.zxhshop.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhshop.mapper.SeckillGoodsMapper;
import com.zxhshop.pojo.TbSeckillGoods;
import com.zxhshop.seckill.service.SeckillGoodsService;
import com.zxhshop.service.impl.BaseServiceImpl;
import com.zxhshop.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.sound.midi.Soundbank;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillGoodsService.class)
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private final static String SECKILL_GOODS = "SECKILL_GOODS";

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillGoods.get***())){
            criteria.andLike("***", "%" + seckillGoods.get***() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoodsList = null;
        seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        if (seckillGoodsList==null || seckillGoodsList.size()==0) {
            Example example = new Example(TbSeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //已经审核
            criteria.andEqualTo("status", "1");
            //库存大于0
            criteria.andGreaterThan("stockCount", 0);
            //开始时间小于或等于当前时间
            criteria.andLessThanOrEqualTo("startTime", new Date());
            //结束时间大于当前时间
            criteria.andGreaterThan("endTime", new Date());
            //按开始时间升序排序
            example.orderBy("startTime");
            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            //将秒杀商品一个一个存入redis中
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                for (TbSeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);
                }
            }
        } else {
            System.out.println("从缓存数据库redis中读取数据...");
        }
        return seckillGoodsList;
    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(id);
    }
}
