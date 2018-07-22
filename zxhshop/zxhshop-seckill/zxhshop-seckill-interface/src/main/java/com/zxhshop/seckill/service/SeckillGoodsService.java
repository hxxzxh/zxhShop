package com.zxhshop.seckill.service;

import com.zxhshop.pojo.TbSeckillGoods;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    /**
     *  查询已经审核库存大于 0 ，开始但是还未结束的秒杀商品列表
     * @return  秒杀商品列表
     */
    List<TbSeckillGoods> findList();


    /**
     * 根据id在redis中查询商品
     * @param id
     * @return 秒杀商品信息
     */
    TbSeckillGoods findOneFromRedis(Long id);
}