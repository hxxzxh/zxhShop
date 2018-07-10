package com.zxhshop.search.service;

import com.zxhshop.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    Map<String,Object> search(Map<String, Object> searchMap);

    /**
     * 批量导入商品列表到solr索引库
     * @param itemList 商品列表
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 根据goodsId商品id集合删除其对应在solr中的商品数据
     * @param goodsIdList goodsId商品id集合
     */
    void deleteItemByGoodsIdList(List<Long> goodsIdList);

//    Map<String, Object> searchItemList(Map<String, Object> searchMap);
}
