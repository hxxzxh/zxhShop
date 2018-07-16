package com.zxh.solr;

import com.zxhshop.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    //新增更新
    @Test
    public void testAdd(){
        TbItem item = new TbItem();
        item.setId(1L);
        item.setTitle("三星 Find X 全面屏");
        item.setBrand("OPPO");
        item.setPrice(new BigDecimal(88888));
        item.setGoodsId(123L);
        item.setSeller("OPPO旗舰店");
        item.setCategory("手机");
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    //根据主键删除
    @Test
    public void testDeleteById() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    //根据条件删除
    @Test
    public void testDeleteByQuery() {
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //根据关键字分页查询
    @Test
    public void testQueryInPage(){
        SimpleQuery query = new SimpleQuery("*:*");
        query.setOffset(20);//分页起始索引号：默认为0
        query.setRows(20);//分页页大小：默认为10
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    private void showPage(ScoredPage<TbItem> scoredPage) {
        System.out.println("总记录数为:" + scoredPage.getTotalElements());
        System.out.println("总页数为：" + scoredPage.getTotalPages());
        List<TbItem> itemList = scoredPage.getContent();
        for (TbItem item : itemList) {
            System.out.println("title:" + item.getTitle() + ", price:" + item.getPrice());
        }
    }

    //多条件查询
    @Test
    public void testMultiQuery(){
        SimpleQuery query = new SimpleQuery();
//        Criteria criteria1 = new Criteria("item_title").is("OPPO");
        Criteria criteria1 = new Criteria("item_title").contains("曜");
        query.addCriteria(criteria1);

//        Criteria criteria2 = new Criteria("item_price").greaterThanEqual(8000);
        Criteria criteria2 = new Criteria("item_price").greaterThanEqual(80);
        query.addCriteria(criteria2);

//        query.setOffset(20);//分页起始索引号：默认为0
//        query.setRows(20);//分页页大小：默认为10
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }













}