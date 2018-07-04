package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbGoods;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.Goods;
import com.zxhshop.vo.PageResult;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    void deleteGoodsByIds(Long[] ids);

    void updateMarketable(Long[] ids, String isMarketable);
}