package com.zxhshop.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zxhshop.pojo.TbItem;
import com.zxhshop.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.StringUtils;

import java.util.*;

@Service(interfaceClass = ItemSearchService.class)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

   /* @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();

        SimpleQuery query = new SimpleQuery();
        //查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //查询
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        List<TbItem> itemList = scoredPage.getContent();
        //设置返回的商品列表
        resultMap.put("rows", itemList);

       *//* Map<String, Object> resultMap = new HashMap<>();

        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        resultMap.put("rows", scoredPage.getContent());*//*

        return resultMap;
    }*/

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();

        //1.搜索商品列表
        resultMap.putAll(searchItemList(searchMap));

        //2.根据搜索关键字搜索商品分类列表
        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);

        //3.获取第一个商品分类对应的品牌和规格列表；如果过滤条件有商品分类则以其为优先
        String categoryName = searchMap.get("category").toString();
        if (StringUtils.isEmpty(categoryName)&&categoryList.size() > 0) {
            resultMap.putAll(getBrandAndSpecList(categoryList.get(0)));
        }else {
            resultMap.putAll(getBrandAndSpecList(categoryName));
        }

        return resultMap;
    }

    /**
     * 根据关键字搜索商品
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    private Map<String, Object> searchItemList(Map<String, Object> searchMap) {

        Map<String, Object> map = new HashMap<>();

        //创建高亮搜索对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照分类过滤
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery categoryFilterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(categoryFilterQuery);
        }

        //按照品牌过滤
        if(!StringUtils.isEmpty(searchMap.get("brand"))){
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery brandFilterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(brandFilterQuery);
        }

        //按照规格gl
        if (searchMap.get("spec") != null) {
            Map<String, Object> specMap = (Map<String, Object>) searchMap.get("spec");
            Set<Map.Entry<String, Object>> entrySet = specMap.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                Criteria specCriteria = new Criteria("item_spec" + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery specFilterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(specFilterQuery);
            }
        }


        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮起始标签
        highlightOptions.setSimplePostfix("</em>");//高亮结束标签
        query.setHighlightOptions(highlightOptions);

        //查询
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮标题
        List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();
        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null) {
                    //设置高亮标题
                    entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                }
            }
        }

        //设置返回列表
        map.put("rows", itemHighlightPage.getContent());

        return map;
    }

    /**
     * 根据搜索关键字搜索商品分类列表
     * @param searchMap 搜索条件
     * @return 商品分类列表
     */
    private List<String> searchCategoryList(Map<String, Object> searchMap) {
        List<String> categoryList = new ArrayList<>();

        SimpleQuery query = new SimpleQuery();

        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分组
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");//分组的域的名称
        query.setGroupOptions(groupOptions);

        //按分组条件查询
        GroupPage<TbItem> itemGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = itemGroupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //将分组查询后的各个商品分类加入到返回的商品集合
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            categoryList.add(groupEntry.getGroupValue());
        }

        return categoryList;
    }


    /**
     * 根据商品分类查询该分类对应的品牌列表和规格列表
     * @param categoryName 商品分类
     * @return 品牌列表和规格列表的map
     */
    private Map<String, Object> getBrandAndSpecList(String categoryName) {
        Map<String, Object> map = new HashMap<>();

        //分类模板id
        Long typeTemplateId = (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);

        if (typeTemplateId != null) {
            //根据分类模板id从redis中获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeTemplateId);
            map.put("brandList", brandList);

            //根据分类模板id从redis中获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeTemplateId);
            map.put("specList", specList);
        }

        return map;
    }

}
